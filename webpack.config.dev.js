const path = require('path');
const webpack = require('webpack');
const Visualizer = require('webpack-visualizer-plugin');

module.exports = {
    entry: ['babel-polyfill', './src/main/client/index.js'],
    output: {
        path: path.resolve(__dirname, './src/main/resources/static'),
        publicPath: '/static',
        filename: 'built/app.js'
    },
    performance: {
        maxAssetSize: 100000,
        maxEntrypointSize: 100000,
        hints: "warning"
    },
    // entry: {
    //     app: './src/main/client/index.js',
    //     configurator: './src/main/client/components/configurator/index.js'
    // },
    // output: {
    //     path: path.resolve(__dirname, './src/main/resources/static/built'),
    //     publicPath: '/built/',
    //     filename: '[name].js',
    //     library: 'sftp-gateway',
    //     libraryTarget: 'umd',
    //     umdNamedDefine: true
    // },
    // externals: {
    //     lodash: {
    //         commonjs: 'lodash',
    //         commonjs2: 'lodash',
    //         amd: 'lodash',
    //         root: '_',
    //     },
    //     react: {
    //         commonjs: "react",
    //         commonjs2: "react",
    //         amd: "React",
    //         root: "React"
    //     },
    //     "react-dom": {
    //         commonjs: "react-dom",
    //         commonjs2: "react-dom",
    //         amd: "ReactDOM",
    //         root: "ReactDOM"
    //     }
    // },
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
        // new webpack.HashedModuleIdsPlugin(),
        new webpack.optimize.OccurrenceOrderPlugin(true),
        // new webpack.optimize.CommonsChunkPlugin({
        //     name: 'vendor'
        // }),
        // new webpack.optimize.LimitChunkCountPlugin({
        //     maxChunks: 1,
        // }),
        new Visualizer({
            filename: './statistics.html'
        }),
        new webpack.LoaderOptionsPlugin({
            minimize: false,
            debug: true
        })
    ],

    resolve: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js', '.jsx']
        // alias: {
        //     'react': path.resolve(__dirname, './node_modules/react'),
        //     'react-dom': path.resolve(__dirname, './node_modules/react-dom'),
        // }
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
                    path.join(__dirname, 'src/main/client')
                ],
                // exclude: /node_modules/,
                options: {
                    compact: true,
                    presets: [
                        [
                            'env',
                            {
                                'targets': {
                                    'node': 8,
                                    'uglify': true
                                },
                                'modules': false
                            }
                        ],
                        'stage-0',
                        'react'
                    ],
                    plugins: ['transform-decorators-legacy']
                }
            }
        ]
    }
};