'use strict';

var path = require("path");

var nodeExternals = require('webpack-node-externals');

module.exports = {
    mode: "development",
    target: "node",
    output: {
        path: path.resolve(__dirname, "../target/dist"),
        filename: 'index.js',
        libraryTarget: 'this'
    },
    module: {
        rules: [
            {
                test: /\.proto$/,
                loader: "pbf-loader"
            }
        ]
    },
    // externals: [nodeExternals()]
};
