angular.module('lodb.widget.main.fox', [])
    .controller('foxCtrl', function ($scope, $http, responseData) {
        $scope.content = responseData;
        $http.post("fox_proxy", // in the moment, we use a proxy to overcome the same origin policy - maybe our app will be deployed side by side with fox so that we can change that
            {
                "input":  encodeURIComponent($scope.content),
                "type": "text",
                "task": "ner",
                "output": "JSON-LD",
                "returnHtml": true
            }).success(function (data) {
                if (data) {
                    $scope.content = decodeURIComponent(data.input);
                }
            });
    }).filter("sanitize", ['$sce', function($sce) {
        return function(htmlCode){
            return $sce.trustAsHtml(htmlCode);
        }
    }]);