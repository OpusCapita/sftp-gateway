const path = require('path');
const webpack = require('webpack');

module.exports = {
    performance: {
        maxAssetSize: 4096000,
        maxEntrypointSize: 4096000,
        hints: "warning"
    },
    entry: {
        app: ['babel-polyfill', './src/main/client/index.js'],
        configurator: './src/main/client/components/configurator/SFTPConfigurator.react.jsx'
    },
    output: {
        path: path.resolve(__dirname, './src/main/resources/static/built'),
        publicPath: '/built',
        filename: '[name].js',
        library: 'sftp-gateway',
        libraryTarget: 'umd',
        umdNamedDefine: true
    },

    bail: true,

    plugins: [
        new webpack.ContextReplacementPlugin(/moment[\/\\]locale$/, /en|de/),
        new webpack.DefinePlugin({
            'process.env.EXPERIMENTAL_FEATURES_ENABLED': JSON.stringify(process.env.EXPERIMENTAL_FEATURES_ENABLED),
        })
    ],

    resolve: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js', '.jsx']
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