/**
 * Created by dhaeb on 12.05.15.
 */

'use strict';

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
            controller: 'sample01Ctrl'
        })
        .otherwise({
            redirectTo: '/01'
        });

}).controller('sample01Ctrl', function($scope, localStorageService){
        var name = 'adfldb';
        var model = localStorageService.get(name);
        if (!model) {
            // set default model for demo purposes
            model = {
                title: "Mariah Carey",
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
                                                        "content": "Mariah Carey ist eine US-amerikanische Pop-, Hip-Hop- und R&B-Sängerin, Songschreiberin, Produzentin und Schauspielerin. Wikipedia Geboren: 27. März 1970 (Alter 45), Huntington, New York, Vereinigte Staaten Ehepartner: Nick Cannon (verh. 2008), Tommy Mottola (verh. 1993–1998) Kinder: Moroccan Scott Cannon, Monroe Cannon"
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
        }
        $scope.name = name;
        $scope.model = model;
        $scope.collapsible = false;
        $scope.maximizable = false;

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            localStorageService.set(name, model);
        });

    });
