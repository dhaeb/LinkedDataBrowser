/**
 * Created by dhaeb on 12.05.15.
 */

'use strict';

angular.module('linked_data_browser', [
    'adf', 'adf.structures.base',
    'adf.widget.markdown', 'adf.widget.linklist',
    'adf.widget.version', 'adf.widget.clock',
    'LocalStorageModule', 'ngRoute'
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
        var name = 'sample-01';
        var model = localStorageService.get(name);
        if (!model) {
            // set default model for demo purposes
            model = {
                title: "Sample 01",
                structure: "4-8",
                rows: [{
                    columns: [{
                        styleClass: "col-md-4",
                        widgets: [{
                            type: "linklist",
                            config: {
                                links: [{
                                    title: "SCM-Manager",
                                    href: "http://www.scm-manager.org"
                                }, {
                                    title: "Github",
                                    href: "https://github.com"
                                }, {
                                    title: "Bitbucket",
                                    href: "https://bitbucket.org"
                                }, {
                                    title: "Stackoverflow",
                                    href: "http://stackoverflow.com"
                                }]
                            },
                            title: "Links"
                        }]
                    }, {
                        styleClass: "col-md-8",
                        widgets: [{
                            type: "markdown",
                            config: {
                                content: "![scm-manager logo](https://bitbucket.org/sdorra/scm-manager/wiki/resources/scm-manager_logo.jpg)\n\nThe easiest way to share and manage your Git, Mercurial and Subversion repositories over http.\n\n* Very easy installation\n* No need to hack configuration files, SCM-Manager is completely configureable from its Web-Interface\n* No Apache and no database installation is required\n* Central user, group and permission management\n* Out of the box support for Git, Mercurial and Subversion\n* Full RESTFul Web Service API (JSON and XML)\n* Rich User Interface\n* Simple Plugin API\n* Useful plugins available ( f.e. Ldap-, ActiveDirectory-, PAM-Authentication)\n* Licensed under the BSD-License"
                            },
                            title: "Markdown"
                        }]
                    }]
                }]
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