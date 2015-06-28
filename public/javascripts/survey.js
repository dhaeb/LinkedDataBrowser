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
        $scope.question = {};
        $scope.question.comment = "";

        $scope.sendSurveyResult = function(){
            var childCount = $('#survey-body > fieldset').length;
            var setFieldCount = Object.size($scope.question);
            var areAllSurveyQuestionsServed = childCount == setFieldCount;
            if(areAllSurveyQuestionsServed){
                $http.post('/survey', $scope.question)
                      .success(function(){
                        alert("Thanks for rating!");
                    }).error(function(data){
                        alert("error during survey: " + data);
                    });
                $("#surveyModal").modal('hide');
            } else {
                alert("You need to fill all survey answers!");
            }
        }
}]);