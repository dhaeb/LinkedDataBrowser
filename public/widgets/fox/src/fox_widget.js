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
    }]).filter("ldbnize_link", ['$sce', 'DEFAULT_ENDPOINT', function($sce) { // change link to a dbpedia resource into a link to the ldb
        return function(htmlTags){

            function startsWith(s) {
                var regex = /^(\/#\/)/i;
                return regex.test(s);
            };

            if(htmlTags !== undefined){
                $("a").attr("href", function(i, oldHref) {
                    if(oldHref === undefined || startsWith(oldHref)){
                        return oldHref;
                    } else {
                        return "/#/" + encodeURIComponent(oldHref);
                    }
                });
                $("a").attr("target", function(i, target) {
                    var parent = "_parent";
                    if(target !== undefined || target != parent){
                        return parent;
                    }
                   return target;
                });
            }
            return htmlTags;
        }
    }]);