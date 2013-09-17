'use strict';

angular.module("Fortscale").directive("bubblesChart", ["$parse", "$timeout", function($parse, $timeout){
    return {
        template: "<div class='bubbles-chart' style='width: 100%; height: 100%; margin: 0 auto'></div>",
        restrict: 'E',
        scope: true,
        replace: true,
        require: "?ngModel",
        link: function postLink(scope, element, attrs, ngModel) {
            var data, settings;
            var resizeEventListenerEnabled;

            scope.$watch(attrs.ngModel, function(chartData){
                data = chartData;
                drawChart();
            });

            scope.$watch(attrs.settings, function(value){
                settings = value;
                drawChart();

                if (settings.refreshOnResize && !resizeEventListenerEnabled)
                    window.addEventListener("resize", drawChart);
                else if (!settings.refreshOnResize && resizeEventListenerEnabled)
                    window.removeEventListener("resize", drawChart);

                if (settings.events){
                    angular.forEach(settings.events, function(eventSettings){
                        element.on(eventSettings.eventName, ".handle-events", function(e){
                            scope.$apply(function(){
                                scope.$emit("widgetEvent", { event: eventSettings, data: e.currentTarget.__data__, widget: scope.widget });
                            });
                        })
                    });
                }
            });

            scope.$on("refresh", function(){
                drawChart();
            });

            attrs.$observe("highlightedMember", function(member){
                highlightMember(member);
            });

            var showTimeoutPromise;
            scope.$on("show", function(){
                $timeout.cancel(showTimeoutPromise);

                showTimeoutPromise = $timeout(function(){
                    drawChart();
                });
            });

            var circles, texts;

            function drawChart(){
                if (!data || !settings)
                    return;

                var parsedData = parseData();
                element[0].innerHTML = "";
                element[0].style.height = settings.height;

                var diameter = Math.min(parseInt(settings.height), element.width(), element.height());

                element[0].style.width = diameter + "px";

                var bubble = d3.layout.pack()
                    .sort(null)
                    .size([diameter, diameter])
                    .padding(1.5);

                var svg = d3.select(element[0]).append("svg")
                    .attr("width", diameter)
                    .attr("height", diameter)
                    .attr("class", "bubble");

                var node = svg.selectAll(".node")
                    .data(bubble.nodes(parsedData).filter(function(d) { return !d.children; }))
                    .enter().append("g")
                    .attr("class", "node")
                    .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

                node.append("title")
                    .text(function(d) {
                        return d.name + " (" + d.value + "):\n" + d.valueNames;
                    });

                circles = node.append("circle")
                    .attr("r", function(d) { return d.r; })
                    .style("fill", function(d) { return getColor(d.value); });

                texts = node.append("text")
                    .attr("dy", ".3em")
                    .style("text-anchor", "middle")
                    .text(function(d) {
                        return d.name.substring(0, d.r / 3.5);
                    });
            }

            function getColor(value){
                if (value === 1)
                    return "#CE7172";

                if (value === 2)
                    return "#EFC739";

                return "#80BFF0";
            }

            function highlightMemberOpacity(elements, member){
                elements && elements.style("opacity", function(d){
                    return !member || ~d.members.indexOf(member) ? 1 : 0.1;
                });
            }
            function highlightMember(member){
                highlightMemberOpacity(circles, member);
                highlightMemberOpacity(texts, member);
            }

            function parseData(){
                var parsedData = [],
                    itemsIndex = {};

                angular.forEach(data, function(item){
                    var indexedItem = itemsIndex[item[settings.itemField]];
                    if (!indexedItem)
                        indexedItem = itemsIndex[item[settings.itemField]] = { name: item[settings.itemField], members: [], value: 0 };

                    indexedItem.members.push(item[settings.childrenField]);
                    indexedItem.value += settings.valueIsCount ? 1 : item[settings.valueField];
                });

                var item;
                for(var itemName in itemsIndex){
                    item = itemsIndex[itemName];
                    item.valueNames = item.members.join("\n");
                    parsedData.push(itemsIndex[itemName]);
                }

                return { children: parsedData };
            }
        }
    };
}]);