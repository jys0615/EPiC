const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://35.216.79.131:8090',
            changeOrigin: true,
            secure: false,
        })
    );
};