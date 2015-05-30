/**
 * Created by dhaeb on 13.05.15.
 */
'use strict';

angular.module('ldbSearchDirective', [])
    .controller('ldbSearchDirectiveController', ['$scope', function($scope) {
        // not used in the moment
    }])
    .directive('ldbSearch', function() {
        return {
            restrict : 'AEC',
            scope: {
                  endpoint: '@endpoint'
            },
            templateUrl: 'assets/angular-templates/ldb_searchtemplate.html'
        };
    });