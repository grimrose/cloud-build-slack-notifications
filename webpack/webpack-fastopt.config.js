'use strict';

var merge = require('webpack-merge');
var core = require('./webpack-core.config.js');

module.exports = merge(core, {
  devtool: "cheap-module-eval-source-map",
  entry: "./functions/entry-point/target/scala-2.12/entry-point-fastopt.js"
});
