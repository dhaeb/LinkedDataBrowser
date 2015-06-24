// using https://github.com/aj0strow/angular-image

angular.module('ngImage', [])

    .directive('img', [
        '$parse',
        function ($parse) {
            function endsWith (url, path) {
                var index = url.length - path.length
                return url.indexOf(path, index) !== -1
            }

            return {
                restrict: 'E',
                link: function (scope, element, attributes) {
                    var fn = attributes.ngError && $parse(attributes.ngError)
                    element.on('error', function (ev) {
                        var src = this.src

                        // If theres an ng-error callback then call it
                        if (fn) {
                            scope.$apply(function () {
                                fn(scope, { $event: ev, $src: src })
                            })
                        }

                        // If theres an ng-error-src then set it
                        if (attributes.ngErrorSrc && !endsWith(src, attributes.ngErrorSrc)) {
                            element.attr('src', attributes.ngErrorSrc)
                        }
                    })
                }
            }
        }
    ]);


angular.module('lodb.widget.main.picture', ['ngImage'])
    .controller('pictureCtrl', function ($scope, responseData, $http, config, widget) {
        $scope.myInterval = 10000;

        var pictures = responseData ;
        $scope.slides = [];

        $scope.report = function (ev, src) {
            $scope.slides = jQuery.grep($scope.slides, function(value){
                return value.image != src;
            });
        };

        var pictureCounter = 0;
        for (var picture in pictures) {
            $scope.slides.push({image: pictures[picture], text: pictures[picture]});
            pictureCounter = pictureCounter+1;
        }
        var googleApiUrl = 'http://ajax.googleapis.com/ajax/services/search/images';
        var query = config.uri.substring(config.uri.lastIndexOf("/") + 1, config.uri.length);
        $http.jsonp(googleApiUrl, {
            params: {
                'v': '1.0',
                'q': query,
                'callback': 'JSON_CALLBACK'
            }
        })
            .success(function (data) {
                if (data) {
                    var picList= data.responseData.results;
                    for (var pic in picList) {
                        $scope.slides.push({image: picList[pic].url, text: picList[pic].url});
                        pictureCounter = pictureCounter+1;
                    }
                }

                if(pictureCounter == 0){
                    config.removeWidget(widget);
                }
            }).
            error(function() {
                if(pictureCounter == 0){
                    config.removeWidget(widget);
                }
            });
    });