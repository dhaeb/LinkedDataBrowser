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
}).service('query_parameter', function(DEFAULT_ENDPOINT, DEFAULT_SUBJECT){
    var endpoint = DEFAULT_ENDPOINT;
    var  subject = DEFAULT_SUBJECT;

    this.getEndpoint = function(){return endpoint;};
    this.setEndpoint = function(newendpoint){endpoint = newendpoint;};

    this.getSubject = function(){return subject;};
    this.setSubject = function(newsubject){subject = newsubject;};

}).controller(controlerName, function($scope, localStorageService, $routeParams, DEFAULT_ENDPOINT, DEFAULT_SUBJECT, query_parameter){
    $scope.$routeParams = $routeParams;
    $scope.endpoint = "endpoint" in $routeParams ? $routeParams.endpoint : query_parameter.getEndpoint();
    $scope.subject = "subject" in $routeParams ? $routeParams.subject : query_parameter.getSubject();
    $scope.query_parameter = query_parameter; // important to watch the value changes!
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
                    if($scope.query_parameter.getSubject() != newValue){
                        $scope.query_parameter.setSubject(newValue);
                    }
                    $scope.subject = newValue;
                    $scope.model =  $scope.modelFactory();
                    $scope.$broadcast(adfDashboardChangedEventName);
                }
            });

            $scope.$watch("endpoint", function(newValue, oldValue){
                if(newValue !== undefined){
                    if($scope.query_parameter.getEndpoint() != newValue){
                        $scope.query_parameter.setEndpoint(newValue);
                    }
                }
            });

        }

        $scope.$watch("query_parameter.getSubject()", function(newValue, oldValue){
            if(newValue !== undefined){
                $scope.subject = newValue;
            }
        }, true);


        $scope.$watch("query_parameter.getEndpoint()", function(newValue, oldValue){
            if(newValue !== undefined){
                $scope.endpoint = newValue;
            }
        });
});
