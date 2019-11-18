const path = require('path');
const webpack = require('webpack');
const Visualizer = require('webpack-visualizer-plugin');

module.exports = {
    devtool: 'eval-source-map',
    cache: true,
    // entry: ['babel-polyfill', './src/main/client/index.js'],
    // output: {
    //     path: path.resolve(__dirname, './src/main/resources/static'),
    //     publicPath: '/static',
    //     filename: 'built/bundle.js'
    // },
    entry: {
        app: ['babel-polyfill', './src/main/client/index.js'],
        configurator: ['babel-polyfill', './src/main/client/components/configurator/index.js']
    },
    devtool: 'eval-source-map',
    cache: true,
    output: {
        path: path.resolve(__dirname, './src/main/resources/static'),
        publicPath: '/static',
        filename: 'built/sftp-gateway-[name].js',
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
            'process.env.NODE_ENV': '"production"',
            'process.traceDeprecation': false
        }),
        new webpack.optimize.OccurrenceOrderPlugin(true),
        /*new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor'
        }),*/
        new Visualizer({
            filename: './statistics.html'
        })
    ],

    resolve: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js']
    },

    resolveLoader: {
        modules: ['NODE_PATH', 'node_modules'],
        extensions: ['.js']
    },

    module: {
        rules: [
            {
                test: /\.css$/,
                loader: "style-loader!css-loader"
            },
            {
                test: /\.less$/,
                loader: 'style-loader!css-loader!less-loader'
            },
            {
                test: /\.sass$/,
                loader: 'style-loader!css-loader!sass-loader'
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
                        '@babel/preset-env',
                        '@babel/preset-react'
                    ],
                    plugins: [
                        '@babel/plugin-proposal-class-properties'
                    ]
                }
            }
        ]
    }
};