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

angular.module('ldbSearchDirective', ['ldbSurvey'])
    .directive('ldbSearch', function() {
        return {
            restrict : 'AEC',
            scope: {
                  endpoint: '@endpoint'
            },
            templateUrl: 'assets/angular-templates/ldb_searchtemplate.html'
        };
    }).controller('ldbSearchDirectiveController', ['$scope', '$http', 'query_parameter', function($scope, $http, query_parameter) {

        $scope.showSurvey = function(){
            $("#surveyModal").modal('show');
        };

        var inputSearchField = $('#search');
        $scope.query_parameter = query_parameter;

        // functions to call when changing parameters in searchbar

        $scope.browse = function(){
            if($scope.searchString == query_parameter.getSubject()){
                query_parameter.setSubject("asdf");
            }
            query_parameter.setSubject($scope.searchString);
        };

        $scope.setendpoint = function(){
            query_parameter.setEndpoint($scope.endpoint);
            alert("Endpoint was set to " + $scope.endpoint)
        };


        // functions to watch and adjust the state with the main page
        $scope.$watch("query_parameter.getSubject()", function(newValue, oldValue){
            if(newValue !== undefined && $scope.searchString != newValue){
                $scope.searchString = ""; // set this empty because its nicer
            }
        });

        $scope.$watch("query_parameter.getEndpoint()", function(newValue, oldValue){
            if(newValue !== undefined && $scope.endpoint != newValue){
                $scope.endpoint = newValue;
            }
        });

        var items = 20; // how many results should be in our suggestion-scrollbar?

        var typeahead = inputSearchField.typeahead({
            highlight: true,
            minLength: 1,
            autoSelect : false,
            items : items,
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
                            'count' : items
                        }
                    }).success(function(response) {
                        typeahead.data('typeahead').source = response;
                    });
                }
            }
        }, true);
}]);