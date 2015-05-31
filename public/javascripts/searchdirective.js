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

        $rootScope.uri = "http://dbpedia.org/resource/DBpedia" // this is our new default resource
        var inputSearchField = $('#search');

        $scope.browse = function(){
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
            source: [],
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
                }
            }
        }, true);
}]);