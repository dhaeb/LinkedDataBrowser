'use strict';

angular.module('lodb.widget.main', ['adf.provider','lodb.widget.main.description','lodb.widget.main.picture'])
  .config(function(dashboardProvider){

    var widget = {
            resolve: {
              responseData: function(defaultService, config){
                if (config.url && config.uri && config.endpoint){
                  return defaultService.get(config.url,config.uri,config.endpoint);
                }
              }
            },
    }
    dashboardProvider
       .widget('default', angular.extend({
            title: 'Default widget',
            description: 'Display full http request',
            templateUrl: 'assets/widgets/default/src/default_view.html',
            controller: 'defaultCtrl',
          },widget))
      .widget('description',angular.extend({
        title: 'Description',
        description: 'Display http request for the description of the uri',
        templateUrl: 'assets/widgets/description/src/description_view.html',
        controller: 'descriptionCtrl',
      },widget))
      .widget('picture',angular.extend({
              title: 'Pictures',
              description: 'Display pictures',
              templateUrl: 'assets/widgets/picture/src/picture_view.html',
              controller: 'pictureCtrl',
            },widget));
  })
  .service('defaultService', function($q, $http){
    return {
      get: function(url,uri,endpoint){
        var deferred = $q.defer();
        $http.get(url,
            {params:{'uri' : uri,
                    'endpoint':endpoint,
            }})
            .success(function(data){
                if (data){
                  deferred.resolve(data);
                } else {
                  deferred.reject();
                }
            })
            .error(function(){
                deferred.reject();
        });
        return deferred.promise;
      }
    };
  })
  .controller('defaultCtrl', function($scope, responseData){
    $scope.responseData = angular.fromJson(responseData);
  })