// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BitsamericasCapacitorPluginBackground",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "BitsamericasCapacitorPluginBackground",
            targets: ["BackgroundModePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "BackgroundModePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BackgroundModePlugin"),
        .testTarget(
            name: "BackgroundModePluginTests",
            dependencies: ["BackgroundModePlugin"],
            path: "ios/Tests/BackgroundModePluginTests")
    ]
)