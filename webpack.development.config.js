const path = require('path');
const webpack = require('webpack');
const Visualizer = require('webpack-visualizer-plugin');

module.exports = {
    performance: {
        maxAssetSize: 4096000,
        maxEntrypointSize: 4096000,
        hints: "warning"
    },
    entry: {
        app: ['babel-polyfill', './src/main/client/index.js'],
        configurator: './src/main/client/components/configurator/index.js'
    },
    output: {
        path: path.resolve(__dirname, './src/main/resources/static/built'),
        publicPath: '/built',
        filename: '[name].js',
        library: 'sftp-gateway',
        libraryTarget: 'umd',
        umdNamedDefine: true
    },
    // externals: {
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
    //     },
    //     "react-router": {
    //         commonjs: "react-router",
    //         commonjs2: "react-router"
    //     },
    //     "@opuscapita/service-base-ui": {
    //         commonjs: "@opuscapita/service-base-ui",
    //         commonjs2: "@opuscapita/service-base-ui",
    //         amd: "@opuscapita/service-base-ui",
    //         root: "@opuscapita/service-base-ui",
    //         umd: "@opuscapita/service-base-ui"
    //     }
    // },
    // node: {
    //     net: 'empty',
    //     tls: 'empty',
    //     dns: 'empty'
    // },

    bail: true,

    plugins: [
        // new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en|de/),
        // new webpack.NoEmitOnErrorsPlugin(),
        // new webpack.DefinePlugin({
        //     'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
        // }),
        // new webpack.optimize.OccurrenceOrderPlugin(true),
        // new Visualizer({
        //     filename: './statistics.html'
        // }),
        // new webpack.LoaderOptionsPlugin({
        //     minimize: true,
        //     debug: false
        // })
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en|de/),
        new webpack.DefinePlugin({
            'process.env.EXPERIMENTAL_FEATURES_ENABLED': JSON.stringify(process.env.EXPERIMENTAL_FEATURES_ENABLED),
        })
    ],

    resolve: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js', '.jsx'],
        // alias: {
        //     'react': path.resolve(__dirname, './node_modules/react'),
        //     'react-dom': path.resolve(__dirname, './node_modules/react-dom'),
        //     'react-router': path.resolve(__dirname, './node_modules/react-router'),
        //     '@opuscapita/service-base-ui': path.resolve('@opuscapita/service-base-ui')
        // }
    },

    resolveLoader: {
        modules: ['node_modules'],
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