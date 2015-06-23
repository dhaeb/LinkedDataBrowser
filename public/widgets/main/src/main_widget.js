'use strict';

angular.module('lodb.widget.main', ['adf.provider', 'adf.youtube',
    'lodb.widget.main.picture', 'lodb.widget.main.fox', 'lodb.widget.main.openlayers'])
    .config(function (dashboardProvider) {

        var mainWidget = {
            resolve: {
                responseData: function (defaultService, config) {
                    if (config.url && config.uri && config.endpoint) {
                        var json = defaultService.get(config.url, config.uri, config.endpoint); // get defered
                        return json.then(function(data){ // map the defered result
                            var responseDataAsJson = angular.fromJson(data);
                            if ('transform' in config) {
                                return config.transform(responseDataAsJson);
                            } else {
                                return responseDataAsJson;
                            }
                        });
                    }
                }
            }
        };

        dashboardProvider
            .widget('default', angular.extend({
                title: 'Content',
                description: 'Display the content of an asynchronous call',
                templateUrl: 'assets/widgets/main/src/default_view.html',
                controller: 'defaultCtrl',
            }, mainWidget))
            .widget('picture', angular.extend({
                title: 'Pictures',
                description: 'Display pictures',
                templateUrl: 'assets/widgets/picture/src/picture_view.html',
                controller: 'pictureCtrl',
            }, mainWidget)).widget('fox', angular.extend({
                title : 'Fox Tagged Description',
                description: 'Display a textual description for the subject tagged by fox',
                templateUrl: 'assets/widgets/fox/src/fox_view.html',
                controller: 'foxCtrl'
            }, mainWidget)).widget('openlayers', angular.extend({
                title: 'Geographic Information',
                description: 'Display Geographic Content',
                templateUrl: 'assets/widgets/openlayers/src/openlayers_widget.html',
                controller: 'openlayersCtrl'
            }, mainWidget)
        );

    }).service('defaultService', function ($q, $http) {
        return {
            get: function (url, uri, endpoint) {
                var deferred = $q.defer();
                $http.get(url,
                    {
                        params: {
                            'uri': uri,
                            'endpoint': endpoint,
                        }
                    })
                    .success(function (data) {
                        if (data) {
                            deferred.resolve(data);
                        } else {
                            deferred.reject();
                        }
                    })
                    .error(function () {
                        deferred.reject();
                    });
                return deferred.promise;
            }
        };
    })
    .controller('defaultCtrl', function ($scope, responseData) {
        $scope.responseData = responseData;
    })