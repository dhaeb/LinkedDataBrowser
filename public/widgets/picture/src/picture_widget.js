angular.module('lodb.widget.main.picture', [])
.controller('pictureCtrl', function($scope,responseData,$http) {
    $scope.myInterval = 5000;
    var pictures = angular.fromJson(responseData);
    var slides = $scope.slides = [];
    for(var picture in pictures){
        slides.push({image: pictures[picture],text:pictures[picture]});
    }
    var googleApiUrl='http://ajax.googleapis.com/ajax/services/search/images';
    $http.get(googleApiUrl,{params:{'v' : '1.0',
                        'q':'Mariah Carey',
                },headers:{
                    'Access-Control-Allow-Origin': "http://ajax.googleapis.com",
                }
                })
                .success(function(data){
                    if (data){
                      var picJson=angular.fromJson(data);
                      $scope.test=picJson;
                    }});
 });