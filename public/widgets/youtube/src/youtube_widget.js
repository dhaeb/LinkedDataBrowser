/**
 * Created by dhaeb on 19.06.15.
 */

var controllerName = 'youtubeCtrl';

// dirty gloabal for checking, if the gapi is loaded. we can't to this with angular as angular takes to long to initalize, we monitor this in angular than, if it is set to true
var gapiLoaded = false


function initGAPI(){
    gapiLoaded = true;
};

function assert(condition, message) {
    if (!condition) {
        message = message || "Assertion failed";
        if (typeof Error !== "undefined") {
            throw new Error(message);
        }
        throw message; // Fallback
    }
}

angular.module('adf.youtube', ['adf.provider'])
    .service('blockingService', function($q, $interval){
        var deferred = $q.defer();

        var interval = $interval(function() {
            if (gapiLoaded) {
                $interval.cancel(interval);
                assert(typeof gapi !== 'undefined');
                deferred.resolve(gapi.client);
            }
            deferred.notify('still waiting for gapi...');
        }, 100)


        return deferred.promise;
    }).config(function (dashboardProvider) {
        dashboardProvider.widget('youtube', {
                title: 'Youtube',
                description: 'Display youtube videos to a given tag.',
                templateUrl: 'assets/widgets/youtube/src/youtube_view.html',
                controller: controllerName
            });

    }).service('gapiService', function() {
        this.initGapi = function(postInitiation) {
            gapi.client.setApiKey("AIzaSyDNOl-8nPjCvldI4BnBaNjRZzxHSQl755c");
            gapi.client.load('youtube', 'v3', postInitiation);
        }
    }).controller(controllerName, function ($scope, $window, config, blockingService, gapiService) {

        var postInitiation = function() {
            // load all your assets
            var request = gapi.client.youtube.search.list({
                part : "snippet",
                type : "video",
                q : encodeURIComponent(config.q).replace(/%20/g, "+"),
                maxResults : 18
                //publishedAfter : "2015-01-01T00:00:00Z"
            });
            var linksContainer = $('#links');
            if (linksContainer.children().length == 0 ) {
                request.execute(function(response){
                    $scope.response = response;
                });
            }
        };

        blockingService.then(function(){
            gapiService.initGapi(postInitiation);
        });

        /*$window.initGapi = function() {
            gapiService.initGapi(postInitiation);
        };*/


    });