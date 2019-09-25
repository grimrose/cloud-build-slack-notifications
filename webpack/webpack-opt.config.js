'use strict';

var merge = require('webpack-merge');
var core = require('./webpack-core.config.js');
var webpack = require("webpack");

module.exports = merge(core, {
  mode: "production",
  devtool: "source-map",
  entry: "./functions/entry-point/target/scala-2.12/entry-point-opt.js",
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify('production')
      }
    })
  ]
});
