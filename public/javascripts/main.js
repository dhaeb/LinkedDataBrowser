/**
 * Created by dhaeb on 12.05.15.
 */

'use strict';

var controlerName = 'sample01Ctrl';
var adfDashboardChangedEventName = 'adfDashboardChanged';
var widgetsPath = '';
var defaultWidgetsPath = 'components.';

angular.module('linked_data_browser', [
    'adf', 'adf.structures.base',
    'adf.widget.markdown', 'adf.widget.linklist',
    'adf.widget.version', 'adf.widget.clock',
    'LocalStorageModule', 'ngRoute',
    'ldbSearchDirective','lodb.widget.main'
]).config(function(dashboardProvider, $routeProvider, localStorageServiceProvider){
    dashboardProvider.widgetsPath(widgetsPath);
    localStorageServiceProvider.setPrefix('adf');
        $routeProvider.when('/01', {
            templateUrl: 'assets/angular-templates/adf.html',
            controller: controlerName
        })
        .otherwise({
            redirectTo: '/01'
        });

}).controller(controlerName, function($scope, localStorageService){
        var name = 'adfldb';
        var model = localStorageService.get(name);
        $scope.modelFactory = function modelFactory(){
            return {
                title: $scope.uri.substring($scope.uri.lastIndexOf('/')+1),
                structure: "9-3 (6-6/12)",
                rows: [{"columns": [
                    {
                        "styleClass": "col-md-3",
                        "widgets": [{
                            "type": "picture",
                            "config": {
                                "uri": $scope.uri,
                                "url": '/nl_from_subject',
                                "endpoint":'http://dbpedia.org/sparql',
                            },
                        }]
                       },
                        {

                            "styleClass": "col-md-9",
                            "rows": [
                                {
                                    "columns": [
                                        {
                                            "styleClass": "col-md-12",
                                            "widgets": [
                                                {
                                                    "type": "description",
                                                    "config": {
                                                        "uri": $scope.uri,
                                                        "url": '/nl_from_subject',
                                                        "endpoint":'http://dbpedia.org/sparql',
                                                    },
                                                }
                                            ]
                                        }
                                    ]
                                }
                              ]
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

            $scope.$watch("uri", function(newValue, oldValue){
                if(newValue !== undefined){
                    $scope.model =  $scope.modelFactory();
                    $scope.$broadcast(adfDashboardChangedEventName);
                }
            });
        }
});












































/*
##orig####----------------------------------------------------------------->
angular.module('linked_data_browser', [
    'adf', 'adf.structures.base',
    'adf.widget.markdown', 'adf.widget.linklist',
    'adf.widget.version', 'adf.widget.clock',
    'LocalStorageModule', 'ngRoute',
    'ldbSearchDirective'
]).config(function(dashboardProvider, $routeProvider, localStorageServiceProvider){
    dashboardProvider.widgetsPath('components/');
    localStorageServiceProvider.setPrefix('adf');
        $routeProvider.when('/01', {
            templateUrl: 'assets/angular-templates/adf.html',
            controller: controlerName
        })
        .otherwise({
            redirectTo: '/01'
        });

}).controller(controlerName, function($scope, localStorageService){
        var name = 'adfldb';
        var model = localStorageService.get(name);
        $scope.modelFactory = function modelFactory(){
            return {
                title: $scope.uri.substring($scope.uri.lastIndexOf('/')+1),
                structure: "9-3 (6-6/12)",
                rows: [{
                    "columns": [
                        {
                            "styleClass": "col-md-9",
                            "rows": [
                                {
                                    "columns": [
                                        {
                                            "styleClass": "col-md-12",
                                            "widgets": [
                                                {
                                                    "type": "markdown",
                                                    "config": {
                                                        "content": $scope.uri
                                                    },
                                                    "title": "Sängerin"
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "columns": [
                                        {
                                            "styleClass": "col-md-6",
                                            "widgets": [
                                                {
                                                    type: "markdown",
                                                    config: {
                                                        content: "Bild,Video,Map,etc..."
                                                    },
                                                    title: "Additional"
                                                }
                                            ]
                                        },
                                        {
                                            "styleClass": "col-md-6",
                                            "widgets": [
                                                {
                                                    type: "markdown",
                                                    config: {
                                                        content: "Bild,Video,Map,etc..."
                                                    },
                                                    title: "Addional"
                                                }
                                            ]
                                        },
                                        {
                                            "styleClass": "col-md-6",
                                            "widgets": [
                                                {
                                                    type: "markdown",
                                                    config: {
                                                        content: "Bild,Video,Map,etc..."
                                                    },
                                                    title: "Additional"
                                                }
                                            ]
                                        },
                                        {
                                            "styleClass": "col-md-6",
                                            "widgets": [
                                                {
                                                    type: "markdown",
                                                    config: {
                                                        content: "Bild,Video,Map,etc..."
                                                    },
                                                    title: "Addional"
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "columns": [
                                        {
                                            "styleClass": "col-md-12",
                                            "widgets": [
                                                {
                                                    "type": "markdown",
                                                    "config": {
                                                        "content": "..."
                                                    },
                                                    "title": "RDF Links"
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        },{
                            "styleClass": "col-md-3",
                            "widgets": [
                                {
                                    "type": "markdown",
                                    "config": {
                                        "content": "<img src=\"http://mariahcareynetwork.com/news/wp-content/uploads/2014/05/fafhonors14-5.jpg\" alt=\"Drawing\" style=\"width: 230px;\"/>"
                                    },
                                    "title": "Pic1"
                                },
                                {
                                    "type": "markdown",
                                    "config": {
                                        "content": "<img src=\"http://www.herald.co.zw/wp-content/uploads/2014/10/mariah-carey-we-belong-together-siik-remix.jpg\" alt=\"Drawing\" style=\"width: 230px;\"/>"
                                    },
                                    "title": "Pic2"
                                }
                            ]
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
            $scope.collapsible = false;
            $scope.maximizable = false;
        $scope.$on('adfDashboardChanged', function (event, name, model) {
            localStorageService.set(name, model);
        });

        $scope.$watch("uri", function(newValue, oldValue){
            if(newValue !== undefined){
                $scope.model =  $scope.modelFactory();
                $scope.$broadcast(adfDashboardChangedEventName);
            }
        });
    }
});
*/
