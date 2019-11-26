using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace TokenBroker.Controllers
{
    using Azure.Identity;
    using Azure.Security.KeyVault.Secrets;
    using Microsoft.Azure.Cosmos;
    using Microsoft.Extensions.Configuration;
    using System.Configuration;
    using System.Net;
    using System;
    using Microsoft.Azure.Services.AppAuthentication;
    using Microsoft.Azure.KeyVault;

    [ApiController]
    [Route("api/[controller]")]
    public class TokenController : ControllerBase
    {
        private const string databaseID = "fakeDB";
        private const string containerID = "tempCollection";
        private static readonly string permissionID = containerID + "PK";

        private readonly IConfiguration _configuration;
        private readonly ILogger<TokenController> _logger;

        private const string kvUri = "https://tokenbroker-0-kv.vault.azure.net/";

        public TokenController(IConfiguration configuration, ILogger<TokenController> logger)
        {
            _configuration = configuration;
            _logger = logger;
        }

        [HttpGet("{userID}", Name = "GetToken")]
        public string Get(string userID)
        {

            try
            {
                var permission = GetPermission(userID);
                return permission.Token;
            }
            catch (KeyNotFoundException e)
            {
                return HttpStatusCode.BadRequest.ToString();
            }
        }

        private PermissionProperties GetPermission(string userID)
        {
            var endpoint = _configuration["ENDPOINT"];
            var key = _configuration["KEY"];
            CosmosClient cosmosClient;

            if (endpoint != null && key != null)
            {
                _logger.LogInformation(kvUri);
                _logger.LogInformation(endpoint);
                cosmosClient = new CosmosClient(endpoint, key);
            }
            else
            {
                throw new Exception("Endpoint or key is null");
            }

            var database = cosmosClient.GetDatabase(databaseID);

            User user = null;

            try
            {
                _logger.LogDebug("User not found, creating user");
                user = database.CreateUserAsync(userID).GetAwaiter().GetResult();
                var container = database.GetContainer(containerID);
                var partitionKey = new PartitionKey(Guid.NewGuid().ToString());

                var permissionProperties = new PermissionProperties(permissionID, PermissionMode.All, container, partitionKey);
                user.CreatePermissionAsync(permissionProperties).Wait();
            }
            catch(Exception e)
            {
                user = database.GetUser(userID);
            }

            if (user != null)
            {
                var permission = user.GetPermission(permissionID).ReadAsync().GetAwaiter().GetResult().Resource;
                return permission;
            }

            throw new KeyNotFoundException();
        }
    }
}
