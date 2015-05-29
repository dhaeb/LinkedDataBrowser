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
           console.log('newValue=' + newValue);
           $scope.searchSuggestionList = [];
           if(newValue !== undefined && newValue.length >= 2){
                //load suggestion from server
                $http.get("/searchsuggestion",{params:{query:newValue}})
                    .success(function(response) {
                        $scope.searchSuggestionList = JSON.parse( response);
                    });

           } else if (newValue !== undefined && newValue.length < 2 ){
                $scope.searchSuggestionList = [];
           }
        });
}]);