/**
 * Created by dhaeb on 19.06.15.
 */
angular.module('lodb.widget.main.openlayers', [])
    .controller('openlayersCtrl', function ($scope, responseData) {

        var vectorSource = new ol.source.Vector({
            //create empty vector
        });

        //create a bunch of icons and add to source vector
        var iconFeature = new ol.Feature({
            geometry: new
                ol.geom.Point(ol.proj.transform([responseData.long, responseData.lat], 'EPSG:4326',   'EPSG:3857')),
            name: "adsf",
            population: 4000,
            rainfall: 500
        });
        vectorSource.addFeature(iconFeature);

        //create the style
        var iconStyle = new ol.style.Style({
            image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
                anchor: [0.5, 46],
                anchorXUnits: 'fraction',
                anchorYUnits: 'pixels',
                opacity: 0.65,
                src: 'http://iconshow.me/download.php?file=path/media/images/Mixed/small-n-flat-icon/png2/48/-map-marker.png' // create commons license
            }))
        });


        var map = new ol.Map({
            target: 'map',
            renderer: 'canvas',
            layers: [
                new ol.layer.Tile({
                    source: new ol.source.OSM()
                }),
                new ol.layer.Vector({
                    source: vectorSource,
                    style: iconStyle
                })
            ],
            view: new ol.View({
                center: ol.proj.transform([responseData.long, responseData.lat], 'EPSG:4326', 'EPSG:3857'),
                zoom: 10
            })
        });
    });