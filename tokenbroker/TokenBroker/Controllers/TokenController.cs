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
    using System.Configuration;
    using System.Net;

    [ApiController]
    [Route("api/[controller]")]
    public class TokenController : ControllerBase
    {
        private const string databaseID = "fakeDB";
        private const string containerID = "tempCollection";
        private static readonly string permissionID = containerID + "PK";

        private readonly ILogger<TokenController> _logger;

        public TokenController(ILogger<TokenController> logger)
        {
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
            var endpoint = ConfigurationManager.AppSettings["ENDPOINT"];
            var key = ConfigurationManager.AppSettings["KEY"];

            var cosmosClient = new CosmosClient(endpoint, key);

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
