/**
    this module merged the widget components to a runnable widget. additional load this module for each
    widget the rest api content.
    The components from the widgets are in the folder public/widgets/«widgetName»
    A default Controller exist to the bottom of this module.

    merge widget description:
        1. import the widget module in load.widget.main module
        2. add in .config the widget parameter in dashboardProvider like:
            .widget('default', angular.extend({
                            title: 'widget default title',
                            description: 'widget default description',
                            templateUrl: 'path to template',
                            controller: 'controller name',
                        }, mainWidget))
*/

'use strict';

angular.module('lodb.widget.main', ['adf.provider', 'adf.youtube',
    'lodb.widget.main.picture', 'lodb.widget.main.fox', 'lodb.widget.main.openlayers'])
    .config(function (dashboardProvider) {
        var mainWidget = {
            resolve: {
                responseData: function (defaultService, config) {

                    //set the function to remove the widget
                    config.removeWidget = function(widget){
                             var removeElementTag='div';
                             var removeElementAttribute='adf-id';
                             var myElements = angular.element(removeElementTag);
                             if (myElements.length != 0) {
                                for(var myElementKey in myElements){
                                var myElement = myElements[myElementKey];
                                if( myElement.attributes != undefined){
                                    if(removeElementAttribute in myElement.attributes){
                                        if(myElement.attributes.getNamedItem(removeElementAttribute).nodeValue == widget.wid){
                                            myElement.remove();
                                            }
                                        }
                                    }
                                }
                             }
                           };

                    //load the content from trhe rest api
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
        //load content from the rest api

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
    .controller('defaultCtrl', function ($scope, responseData,widget,config) {
        //this is the example controller
        //to remove a widget, run the function: config.removeWidget(widget);
        //in config are the config parameters, that you can defined in the main.js
        $scope.responseData = responseData;
    })