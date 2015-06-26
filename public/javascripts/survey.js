/**
 * Created by dhaeb on 13.05.15.
 */
'use strict';


Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

angular.module('ldbSurvey', [])
    .controller('ldbSurveyController', ['$scope', '$http', function($scope, $http) {

        $scope.sendSurveyResult = function(){
            var areAllSurveyQuestionsServed = $('#survey-body').children().length == Object.size($scope.question);
            if(areAllSurveyQuestionsServed){
                $http.post('/survey', $scope.question);
                alert("Thanks for rating! :-)");
                $("#surveyModal").modal('hide');
            } else {
                alert("You need to fill all survey answers!");
            }
        }
}]);