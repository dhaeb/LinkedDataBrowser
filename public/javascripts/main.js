/**
 * Created by dhaeb on 12.05.15.
 */

'use strict';

var controlerName = 'sample01Ctrl';
var adfDashboardChangedEventName = 'adfDashboardChanged';
var widgetsPath = '';

angular.module('linked_data_browser', [
    'adf', 'adf.structures.base',
    'adf.widget.markdown', 'adf.widget.linklist',
    'adf.widget.version', 'adf.widget.clock',
    'LocalStorageModule', 'ngRoute',
    'ldbSearchDirective','lodb.widget.main'
]).config(function(dashboardProvider, $routeProvider, localStorageServiceProvider){
    dashboardProvider.widgetsPath(widgetsPath);
    localStorageServiceProvider.setPrefix('adf');

        $routeProvider.when('/', {
            templateUrl: 'assets/angular-templates/adf.html',
            controller: controlerName
        }).when("/:endpoint*\/subject/:subject*", {
            templateUrl: 'assets/angular-templates/adf.html',
            controller: controlerName
        }).when("/:subject*", {
            templateUrl: 'assets/angular-templates/adf.html',
            controller: controlerName
        })
        .otherwise({
            redirectTo: '/'
        });
}).controller(controlerName, function($scope, localStorageService, $routeParams, DEFAULT_ENDPOINT, DEFAULT_SUBJECT){
    $scope.endpoint = "endpoint" in $routeParams ? $routeParams.endpoint : DEFAULT_ENDPOINT;
    $scope.subject = "subject" in $routeParams ? $routeParams.subject : DEFAULT_SUBJECT;
        var name = 'adfldb';
        var model = localStorageService.get(name);
        $scope.modelFactory = function modelFactory(){
            return {
                title: $scope.subject.substring($scope.subject.lastIndexOf('/')+1),
                structure: "8-4 (6-6/12)",
                rows: [{"columns": [
                        {

                            "styleClass": "col-md-7",
                            "rows": [
                                {
                                    "columns": [
                                        {
                                            "styleClass": "col-md-12",
                                            "widgets": [
                                                {
                                                    "title" : "Description",
                                                    "type": "fox",
                                                    "config": {
                                                        "uri": $scope.subject,
                                                        "url": '/nl_from_subject',
                                                        "endpoint": $scope.endpoint,
                                                        "transform"  : function(j){return j.nl;}
                                                    },
                                                }
                                            ]
                                        }
                                    ]
                                }
                              ]
                        },
                        {
                                                "styleClass": "col-md-5",
                                                "widgets": [{
                                                    "type": "picture",
                                                    "config": {
                                                        "uri": $scope.subject,
                                                        "url": '/pictures_from_subject',
                                                        "endpoint": $scope.endpoint,
                                                    },
                                                }]
                                               }
                    ]
                }
                ]
            };
        };

        if (!model) {
            $scope.editable = false;
            // set default model for demo purposes
            model = $scope.modelFactory();
            $scope.name = name;
            $scope.model = model;
            $scope.collapsible = true;
            $scope.maximizable = false;

            $scope.$on('adfDashboardChanged', function (event, name, model) {
                localStorageService.set(name, model);
            });

            $scope.$watch("subject", function(newValue, oldValue){
                if(newValue !== undefined){
                    $scope.subject = newValue;
                    $scope.model =  $scope.modelFactory();
                    $scope.$broadcast(adfDashboardChangedEventName);
                }
            });

            $scope.$watch("subject_mask", function(newValue, oldValue){
                if(newValue !== undefined && newValue != oldValue){
                    $scope.subject = newValue;
                }
            });

            $scope.$watch("endpoint_mask", function(newValue, oldValue){
                if(newValue !== undefined && newValue != oldValue){
                    $scope.endpoint= newValue;
                }
            });
        }
});
