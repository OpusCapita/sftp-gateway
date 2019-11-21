const path = require('path');
const webpack = require('webpack');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const Visualizer = require('webpack-visualizer-plugin');

module.exports = {
    // entry: ['babel-polyfill', './src/main/client/index.js'],
    // output: {
    //     path: path.resolve(__dirname, './src/main/resources/static'),
    //     publicPath: '/static',
    //     filename: 'built/bundle.js'
    // },
    performance: {
        maxAssetSize: 100000,
        maxEntrypointSize: 100000,
        hints: "warning"
    },
    entry: {
        app: ['./src/main/client/index.js'],
        configurator: ['babel-polyfill', './src/main/client/components/configurator/index.js']
    },
    output: {
        path: path.resolve(__dirname, './src/main/resources/static'),
        publicPath: '/static',
        filename: 'built/sftp-gateway-[name].js',
        chunkFilename: 'built/sftp-gateway-[name].js',
        library: 'sftp-gateway-[name]',
        libraryTarget: 'umd',
        umdNamedDefine: true
    },

    //exclude empty dependencies, require for Joi
    node: {
        net: 'empty',
        tls: 'empty',
        dns: 'empty'
    },

    bail: true,

    plugins: [
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en|de/),
        new webpack.NoEmitOnErrorsPlugin(),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
        }),
        new webpack.optimize.OccurrenceOrderPlugin(true),
        /*new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor'
        }),*/
        new Visualizer({
            filename: './statistics.html'
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: true,
            debug: true
        })
    ],

    resolve: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js', '.jsx']
    },

    resolveLoader: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js', '.jsx']
    },

    module: {
        rules: [
            {
                test: /\.css$/,
                loader: "style-loader!css-loader"
            },
            {
                test: /.jsx?$/,
                loader: 'babel-loader',
                include: [
                    path.join(__dirname, 'src')
                ],
                // exclude: /node_modules/,
                options: {
                    compact: true,
                    presets: [
                        ['env', {'targets': {'node': 8, 'uglify': true}, 'modules': 'umd'}],
                        'stage-0',
                        'react'
                    ],
                    plugins: ['transform-decorators-legacy']
                }
            }
        ]
    }
};