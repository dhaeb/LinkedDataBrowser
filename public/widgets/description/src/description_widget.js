angular.module('lodb.widget.main.description', [])
.controller('descriptionCtrl', function($scope,responseData) {
     $scope.description = angular.fromJson(responseData).nl;
 });