const path = require('path');
const webpack = require('webpack');
const Visualizer = require('webpack-visualizer-plugin');

module.exports = {
    performance: {
        maxAssetSize: 100000,
        maxEntrypointSize: 100000,
        hints: "warning"
    },
    entry: {
        app: ['babel-polyfill', './src/main/client/index.js'],
        configurator: ['./src/main/client/components/configurator/index.js']
    },
    output: {
        path: path.resolve(__dirname, './src/main/resources/static/built'),
        publicPath: '/built/',
        filename: '[name].js',
        library: 'sftp-gateway',
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