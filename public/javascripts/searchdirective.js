/**
 * Created by dhaeb on 13.05.15.
 */
'use strict';

angular.module('ldbSearchDirective', [])
    .directive('ldbSearch', function() {
        return {
            restrict : 'AEC',
            scope: {
                  endpoint: '@endpoint'
            },
            templateUrl: 'assets/angular-templates/ldb_searchtemplate.html'
        };
    }).controller('ldbSearchDirectiveController', ['$scope', '$http', function($scope,$http) {

        $scope.onSelect = function ($item, $model, $label) {
            $scope.$item = $item;
            $scope.$model = $model;
            $scope.$label = $label;
            alert($item.uri);
        };

        $scope.$watch('searchString',function (newValue, oldValue) {
           $scope.searchSuggestionList = [];
           if(newValue !== undefined && newValue.length >= 2){
                //load suggestion from server
                $http.get("/searchsuggestion",{
                    params: {
                        'query' : newValue,
                        'count' : 10
                    }
                 }).success(function(response) {
                    $scope.searchSuggestionList = response;
                 });
           } else if (newValue !== undefined && newValue.length < 2 ){
                $scope.searchSuggestionList = [];
           }
        });
}]);