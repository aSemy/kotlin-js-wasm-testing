
// var initJasmine = function (files) {
//     var jasminePath = path.dirname(require.resolve('jasmine-core'))
//     files.unshift(createPattern(path.join(__dirname, '/adapter.js')))
//     files.unshift(createPattern(path.join(__dirname, '/boot.js')))
//     files.unshift(createPattern(jasminePath + '/jasmine-core/jasmine.js'))
// }
//
// initJasmine.$inject = ['config.files']
//


// Add parameters to the function to receive requested services.
function karmaKotestJsFactory(config) {
    console.log('Hello, karma-kotest-js!');

    config.files.unshift({
        pattern: __dirname + '/karma-kotest-js-plugin.js',
        included: true,
        served: true,
        watched: false
    })
}

// Declare DI tokens plugin wants to inject.
karmaKotestJsFactory.$inject = ['config']

// const framework = {
module.exports = {
    'framework:karma-kotest-js': ['factory', karmaKotestJsFactory]
};
