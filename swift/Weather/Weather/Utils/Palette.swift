import SwiftUI

func ColorFromRGB(rgb: UInt) -> Color {
    return Color(
        red: Double((rgb & 0xFF0000) >> 16) / 255.0,
        green: Double((rgb & 0x00FF00) >> 8) / 255.0,
        blue: Double(rgb & 0x0000FF) / 255.0
    )
}

struct Palette {
    static let color1 = ColorFromRGB(rgb: 0x0F32E3)
    static let color2 = ColorFromRGB(rgb: 0xC6C6B3)
    static let color3 = ColorFromRGB(rgb: 0xABB7D2)
    static let color4 = ColorFromRGB(rgb: 0x506ED1)
    static let color5 = ColorFromRGB(rgb: 0x65737E)
}
