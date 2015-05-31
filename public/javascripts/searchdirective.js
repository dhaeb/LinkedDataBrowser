/**
 * Created by dhaeb on 13.05.15.
 */
'use strict';


String.prototype.startsWith = function (str){
    return this.indexOf(str) === 0;
};

function isUri(uri){
    return uri.startsWith("http://") || uri.startsWith("https://");
}

angular.module('ldbSearchDirective', [])
    .directive('ldbSearch', function() {
        return {
            restrict : 'AEC',
            scope: {
                  endpoint: '@endpoint'
            },
            templateUrl: 'assets/angular-templates/ldb_searchtemplate.html'
        };
    }).controller('ldbSearchDirectiveController', ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http) {

        $scope.onSelect = function ($item, $model, $label) {
            $scope.$item = $item;
            $scope.$model = $model;
            $scope.$label = $label;
            alert($item.uri);
        };

        $scope.searchSuggestionList = [];

        var inputSearchField = $('#search');

        $scope.browse = function(){
            console.log($scope.searchString);
            $rootScope.uri = $scope.searchString;
        };

        var typeahead = inputSearchField.typeahead({
            highlight: true,
            minLength: 1,
            autoSelect : false,
            displayText : function(item){
                if(typeof(item) === 'string'){
                    return item;
                } else {
                    return item.label + " (" +  item.uri + ")";
                }
            },
            afterSelect : function(item){
                return item.uri;
            },
            updater : function(item){
                return item.uri;
            }
        }, {
            name: 'search suggestion views',
            source: $scope.searchSuggestionList,
            templateUrl: 'assets/angular-templates/searchSuggestionTemplate.html'
        });

        $scope.$watch('searchString',function (newValue, oldValue) {
            if(newValue !== undefined && !isUri(newValue)){
                var minsize = 3
                if(newValue.length >= minsize){
                    //load suggestion from server
                    $http.get("/searchsuggestion",{
                        params: {
                            'query' : newValue,
                            'count' : 10
                        }
                    }).success(function(response) {
                        typeahead.data('typeahead').source = response;
                    });
                } else if (newValue !== undefined && newValue.length < minsize ){
                    $scope.searchSuggestionList = [];
                }
            }
        }, true);
}]);