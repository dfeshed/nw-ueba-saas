(function(){var e=window.AmCharts;e.AmRectangularChart=e.Class({inherits:e.AmCoordinateChart,construct:function(a){e.AmRectangularChart.base.construct.call(this,a);this.theme=a;this.createEvents("zoomed","changed");this.marginRight=this.marginBottom=this.marginTop=this.marginLeft=20;this.depth3D=this.angle=0;this.plotAreaFillColors="#FFFFFF";this.plotAreaFillAlphas=0;this.plotAreaBorderColor="#000000";this.plotAreaBorderAlpha=0;this.maxZoomFactor=20;this.zoomOutButtonImageSize=19;this.zoomOutButtonImage=
"lens";this.zoomOutText="Show all";this.zoomOutButtonColor="#e5e5e5";this.zoomOutButtonAlpha=0;this.zoomOutButtonRollOverAlpha=1;this.zoomOutButtonPadding=8;this.trendLines=[];this.autoMargins=!0;this.marginsUpdated=!1;this.autoMarginOffset=10;e.applyTheme(this,a,"AmRectangularChart")},initChart:function(){e.AmRectangularChart.base.initChart.call(this);this.updateDxy();!this.marginsUpdated&&this.autoMargins&&(this.resetMargins(),this.drawGraphs=!1);this.processScrollbars();this.updateMargins();this.updatePlotArea();
this.updateScrollbars();this.updateTrendLines();this.updateChartCursor();this.updateValueAxes();this.scrollbarOnly||this.updateGraphs()},drawChart:function(){e.AmRectangularChart.base.drawChart.call(this);this.drawPlotArea();if(e.ifArray(this.chartData)){var a=this.chartCursor;a&&a.draw()}},resetMargins:function(){var a={},b;if("xy"==this.type){var c=this.xAxes,d=this.yAxes;for(b=0;b<c.length;b++){var g=c[b];g.ignoreAxisWidth||(g.setOrientation(!0),g.fixAxisPosition(),a[g.position]=!0)}for(b=0;b<
d.length;b++)c=d[b],c.ignoreAxisWidth||(c.setOrientation(!1),c.fixAxisPosition(),a[c.position]=!0)}else{d=this.valueAxes;for(b=0;b<d.length;b++)c=d[b],c.ignoreAxisWidth||(c.setOrientation(this.rotate),c.fixAxisPosition(),a[c.position]=!0);(b=this.categoryAxis)&&!b.ignoreAxisWidth&&(b.setOrientation(!this.rotate),b.fixAxisPosition(),b.fixAxisPosition(),a[b.position]=!0)}a.left&&(this.marginLeft=0);a.right&&(this.marginRight=0);a.top&&(this.marginTop=0);a.bottom&&(this.marginBottom=0);this.fixMargins=
a},measureMargins:function(){var a=this.valueAxes,b,c=this.autoMarginOffset,d=this.fixMargins,g=this.realWidth,e=this.realHeight,f=c,k=c,m=g;b=e;var l;for(l=0;l<a.length;l++)a[l].handleSynchronization(),b=this.getAxisBounds(a[l],f,m,k,b),f=Math.round(b.l),m=Math.round(b.r),k=Math.round(b.t),b=Math.round(b.b);if(a=this.categoryAxis)b=this.getAxisBounds(a,f,m,k,b),f=Math.round(b.l),m=Math.round(b.r),k=Math.round(b.t),b=Math.round(b.b);d.left&&f<c&&(this.marginLeft=Math.round(-f+c),!isNaN(this.minMarginLeft)&&
this.marginLeft<this.minMarginLeft&&(this.marginLeft=this.minMarginLeft));d.right&&m>=g-c&&(this.marginRight=Math.round(m-g+c),!isNaN(this.minMarginRight)&&this.marginRight<this.minMarginRight&&(this.marginRight=this.minMarginRight));d.top&&k<c+this.titleHeight&&(this.marginTop=Math.round(this.marginTop-k+c+this.titleHeight),!isNaN(this.minMarginTop)&&this.marginTop<this.minMarginTop&&(this.marginTop=this.minMarginTop));d.bottom&&b>e-c&&(this.marginBottom=Math.round(this.marginBottom+b-e+c),!isNaN(this.minMarginBottom)&&
this.marginBottom<this.minMarginBottom&&(this.marginBottom=this.minMarginBottom));this.initChart()},getAxisBounds:function(a,b,c,d,e){if(!a.ignoreAxisWidth){var h=a.labelsSet,f=a.tickLength;a.inside&&(f=0);if(h)switch(h=a.getBBox(),a.position){case "top":a=h.y;d>a&&(d=a);break;case "bottom":a=h.y+h.height;e<a&&(e=a);break;case "right":a=h.x+h.width+f+3;c<a&&(c=a);break;case "left":a=h.x-f,b>a&&(b=a)}}return{l:b,t:d,r:c,b:e}},drawZoomOutButton:function(){var a=this;if(!a.zbSet){var b=a.container.set();
a.zoomButtonSet.push(b);var c=a.color,d=a.fontSize,g=a.zoomOutButtonImageSize,h=a.zoomOutButtonImage.replace(/\.[a-z]*$/i,""),f=e.lang.zoomOutText||a.zoomOutText,k=a.zoomOutButtonColor,m=a.zoomOutButtonAlpha,l=a.zoomOutButtonFontSize,p=a.zoomOutButtonPadding;isNaN(l)||(d=l);(l=a.zoomOutButtonFontColor)&&(c=l);var l=a.zoomOutButton,q;l&&(l.fontSize&&(d=l.fontSize),l.color&&(c=l.color),l.backgroundColor&&(k=l.backgroundColor),isNaN(l.backgroundAlpha)||(a.zoomOutButtonRollOverAlpha=l.backgroundAlpha));
var r=l=0;void 0!==a.pathToImages&&h&&(q=a.container.image(a.pathToImages+h+a.extension,0,0,g,g),e.setCN(a,q,"zoom-out-image"),b.push(q),q=q.getBBox(),l=q.width+5);void 0!==f&&(c=e.text(a.container,f,c,a.fontFamily,d,"start"),e.setCN(a,c,"zoom-out-label"),d=c.getBBox(),r=q?q.height/2-3:d.height/2,c.translate(l,r),b.push(c));q=b.getBBox();c=1;e.isModern||(c=0);k=e.rect(a.container,q.width+2*p+5,q.height+2*p-2,k,1,1,k,c);k.setAttr("opacity",m);k.translate(-p,-p);e.setCN(a,k,"zoom-out-bg");b.push(k);
k.toBack();a.zbBG=k;q=k.getBBox();b.translate(a.marginLeftReal+a.plotAreaWidth-q.width+p,a.marginTopReal+p);b.hide();b.mouseover(function(){a.rollOverZB()}).mouseout(function(){a.rollOutZB()}).click(function(){a.clickZB()}).touchstart(function(){a.rollOverZB()}).touchend(function(){a.rollOutZB();a.clickZB()});for(m=0;m<b.length;m++)b[m].attr({cursor:"pointer"});void 0!==a.zoomOutButtonTabIndex&&(b.setAttr("tabindex",a.zoomOutButtonTabIndex),b.setAttr("role","menuitem"),b.keyup(function(b){13==b.keyCode&&
a.clickZB()}));a.zbSet=b}},rollOverZB:function(){this.rolledOverZB=!0;this.zbBG.setAttr("opacity",this.zoomOutButtonRollOverAlpha)},rollOutZB:function(){this.rolledOverZB=!1;this.zbBG.setAttr("opacity",this.zoomOutButtonAlpha)},clickZB:function(){this.rolledOverZB=!1;this.zoomOut()},zoomOut:function(){this.zoomOutValueAxes()},drawPlotArea:function(){var a=this.dx,b=this.dy,c=this.marginLeftReal,d=this.marginTopReal,g=this.plotAreaWidth-1,h=this.plotAreaHeight-1,f=this.plotAreaFillColors,k=this.plotAreaFillAlphas,
m=this.plotAreaBorderColor,l=this.plotAreaBorderAlpha;"object"==typeof k&&(k=k[0]);f=e.polygon(this.container,[0,g,g,0,0],[0,0,h,h,0],f,k,1,m,l,this.plotAreaGradientAngle);e.setCN(this,f,"plot-area");f.translate(c+a,d+b);this.set.push(f);0!==a&&0!==b&&(f=this.plotAreaFillColors,"object"==typeof f&&(f=f[0]),f=e.adjustLuminosity(f,-.15),g=e.polygon(this.container,[0,a,g+a,g,0],[0,b,b,0,0],f,k,1,m,l),e.setCN(this,g,"plot-area-bottom"),g.translate(c,d+h),this.set.push(g),a=e.polygon(this.container,[0,
0,a,a,0],[0,h,h+b,b,0],f,k,1,m,l),e.setCN(this,a,"plot-area-left"),a.translate(c,d),this.set.push(a));(c=this.bbset)&&this.scrollbarOnly&&c.remove()},updatePlotArea:function(){var a=this.updateWidth(),b=this.updateHeight(),c=this.container;this.realWidth=a;this.realWidth=b;c&&this.container.setSize(a,b);var c=this.marginLeftReal,d=this.marginTopReal,a=a-c-this.marginRightReal-this.dx,b=b-d-this.marginBottomReal;1>a&&(a=1);1>b&&(b=1);this.plotAreaWidth=Math.round(a);this.plotAreaHeight=Math.round(b);
this.plotBalloonsSet.translate(c,d)},updateDxy:function(){this.dx=Math.round(this.depth3D*Math.cos(this.angle*Math.PI/180));this.dy=Math.round(-this.depth3D*Math.sin(this.angle*Math.PI/180));this.d3x=Math.round(this.columnSpacing3D*Math.cos(this.angle*Math.PI/180));this.d3y=Math.round(-this.columnSpacing3D*Math.sin(this.angle*Math.PI/180))},updateMargins:function(){var a=this.getTitleHeight();this.titleHeight=a;this.marginTopReal=this.marginTop-this.dy;this.fixMargins&&!this.fixMargins.top&&(this.marginTopReal+=
a);this.marginBottomReal=this.marginBottom;this.marginLeftReal=this.marginLeft;this.marginRightReal=this.marginRight},updateValueAxes:function(){var a=this.valueAxes,b;for(b=0;b<a.length;b++){var c=a[b];this.setAxisRenderers(c);this.updateObjectSize(c)}},setAxisRenderers:function(a){a.axisRenderer=e.RecAxis;a.guideFillRenderer=e.RecFill;a.axisItemRenderer=e.RecItem;a.marginsChanged=!0},updateGraphs:function(){var a=this.graphs,b;for(b=0;b<a.length;b++){var c=a[b];c.index=b;c.rotate=this.rotate;this.updateObjectSize(c)}},
updateObjectSize:function(a){a.width=this.plotAreaWidth-1;a.height=this.plotAreaHeight-1;a.x=this.marginLeftReal;a.y=this.marginTopReal;a.dx=this.dx;a.dy=this.dy},updateChartCursor:function(){var a=this.chartCursor;a&&(a=e.processObject(a,e.ChartCursor,this.theme),this.updateObjectSize(a),this.addChartCursor(a),a.chart=this)},processScrollbars:function(){var a=this.chartScrollbar;a&&(a=e.processObject(a,e.ChartScrollbar,this.theme),this.addChartScrollbar(a))},updateScrollbars:function(){},removeChartCursor:function(){e.callMethod("destroy",
[this.chartCursor]);this.chartCursor=null},zoomTrendLines:function(){var a=this.trendLines,b;for(b=0;b<a.length;b++){var c=a[b];c.valueAxis.recalculateToPercents?c.set&&c.set.hide():(c.x=this.marginLeftReal,c.y=this.marginTopReal,c.draw())}},handleCursorValueZoom:function(){},addTrendLine:function(a){this.trendLines.push(a)},zoomOutValueAxes:function(){for(var a=this.valueAxes,b=0;b<a.length;b++)a[b].zoomOut()},removeTrendLine:function(a){var b=this.trendLines,c;for(c=b.length-1;0<=c;c--)b[c]==a&&
b.splice(c,1)},adjustMargins:function(a,b){var c=a.position,d=a.scrollbarHeight+a.offset;a.enabled&&("top"==c?b?this.marginLeftReal+=d:this.marginTopReal+=d:b?this.marginRightReal+=d:this.marginBottomReal+=d)},getScrollbarPosition:function(a,b,c){var d="bottom",e="top";a.oppositeAxis||(e=d,d="top");a.position=b?"bottom"==c||"left"==c?d:e:"top"==c||"right"==c?d:e},updateChartScrollbar:function(a,b){if(a){a.rotate=b;var c=this.marginTopReal,d=this.marginLeftReal,e=a.scrollbarHeight,h=this.dx,f=this.dy,
k=a.offset;"top"==a.position?b?(a.y=c,a.x=d-e-k):(a.y=c-e+f-k,a.x=d+h):b?(a.y=c+f,a.x=d+this.plotAreaWidth+h+k):(a.y=c+this.plotAreaHeight+k,a.x=this.marginLeftReal)}},showZB:function(a){var b=this.zbSet;a&&(b=this.zoomOutText,""!==b&&b&&this.drawZoomOutButton());if(b=this.zbSet)this.zoomButtonSet.push(b),a?b.show():b.hide(),this.rollOutZB()},handleReleaseOutside:function(a){e.AmRectangularChart.base.handleReleaseOutside.call(this,a);(a=this.chartCursor)&&a.handleReleaseOutside&&a.handleReleaseOutside()},
handleMouseDown:function(a){e.AmRectangularChart.base.handleMouseDown.call(this,a);var b=this.chartCursor;b&&b.handleMouseDown&&!this.rolledOverZB&&b.handleMouseDown(a)},update:function(){e.AmRectangularChart.base.update.call(this);this.chartCursor&&this.chartCursor.update&&this.chartCursor.update()},handleScrollbarValueZoom:function(a){this.relativeZoomValueAxes(a.target.valueAxes,a.relativeStart,a.relativeEnd);this.zoomAxesAndGraphs()},zoomValueScrollbar:function(a){if(a&&a.enabled){var b=a.valueAxes[0],
c=b.relativeStart,d=b.relativeEnd;b.reversed&&(d=1-c,c=1-b.relativeEnd);a.percentZoom(c,d)}},zoomAxesAndGraphs:function(){if(!this.scrollbarOnly){var a=this.valueAxes,b;for(b=0;b<a.length;b++)a[b].zoom(this.start,this.end);a=this.graphs;for(b=0;b<a.length;b++)a[b].zoom(this.start,this.end);(b=this.chartCursor)&&b.clearSelection();this.zoomTrendLines()}},handleValueAxisZoomReal:function(a,b){var c=a.relativeStart,d=a.relativeEnd;if(c>d)var e=c,c=d,d=e;this.relativeZoomValueAxes(b,c,d);this.updateAfterValueZoom()},
updateAfterValueZoom:function(){this.zoomAxesAndGraphs();this.zoomScrollbar()},relativeZoomValueAxes:function(a,b,c){b=e.fitToBounds(b,0,1);c=e.fitToBounds(c,0,1);if(b>c){var d=b;b=c;c=d}var d=1/this.maxZoomFactor,g=e.getDecimals(d)+4;c-b<d&&(c=b+(c-b)/2,b=c-d/2,c+=d/2);b=e.roundTo(b,g);c=e.roundTo(c,g);d=!1;if(a){for(g=0;g<a.length;g++){var h=a[g].zoomToRelativeValues(b,c,!0);h&&(d=h)}this.showZB()}return d},addChartCursor:function(a){e.callMethod("destroy",[this.chartCursor]);a&&(this.listenTo(a,
"moved",this.handleCursorMove),this.listenTo(a,"zoomed",this.handleCursorZoom),this.listenTo(a,"zoomStarted",this.handleCursorZoomStarted),this.listenTo(a,"panning",this.handleCursorPanning),this.listenTo(a,"onHideCursor",this.handleCursorHide));this.chartCursor=a},handleCursorChange:function(){},handleCursorMove:function(a){var b,c=this.valueAxes;for(b=0;b<c.length;b++)a.panning||c[b].showBalloon(a.x,a.y)},handleCursorZoom:function(a){if(this.skipZoomed)this.skipZoomed=!1;else{var b=this.startX0,
c=this.endX0,d=this.endY0,e=this.startY0,h=a.startX,f=a.endX,k=a.startY,m=a.endY;this.startX0=this.endX0=this.startY0=this.endY0=NaN;this.handleCursorZoomReal(b+h*(c-b),b+f*(c-b),e+k*(d-e),e+m*(d-e),a)}},handleCursorHide:function(){var a,b=this.valueAxes;for(a=0;a<b.length;a++)b[a].hideBalloon();b=this.graphs;for(a=0;a<b.length;a++)b[a].hideBalloonReal()}})})();(function(){var e=window.AmCharts;e.AmXYChart=e.Class({inherits:e.AmRectangularChart,construct:function(a){this.type="xy";e.AmXYChart.base.construct.call(this,a);this.cname="AmXYChart";this.theme=a;this.createEvents("zoomed");e.applyTheme(this,a,this.cname)},initChart:function(){e.AmXYChart.base.initChart.call(this);this.dataChanged&&this.updateData();this.drawChart();!this.marginsUpdated&&this.autoMargins&&(this.marginsUpdated=!0,this.measureMargins());var a=this.marginLeftReal,b=this.marginTopReal,
c=this.plotAreaWidth,d=this.plotAreaHeight;this.graphsSet.clipRect(a,b,c,d);this.bulletSet.clipRect(a,b,c,d);this.trendLinesSet.clipRect(a,b,c,d);this.drawGraphs=!0;this.showZB()},prepareForExport:function(){var a=this.bulletSet;a.clipPath&&this.container.remove(a.clipPath)},createValueAxes:function(){var a=[],b=[];this.xAxes=a;this.yAxes=b;var c=this.valueAxes,d,g;for(g=0;g<c.length;g++){d=c[g];var h=d.position;if("top"==h||"bottom"==h)d.rotate=!0;d.setOrientation(d.rotate);h=d.orientation;"V"==
h&&b.push(d);"H"==h&&a.push(d)}0===b.length&&(d=new e.ValueAxis(this.theme),d.rotate=!1,d.setOrientation(!1),c.push(d),b.push(d));0===a.length&&(d=new e.ValueAxis(this.theme),d.rotate=!0,d.setOrientation(!0),c.push(d),a.push(d));for(g=0;g<c.length;g++)this.processValueAxis(c[g],g);a=this.graphs;for(g=0;g<a.length;g++)this.processGraph(a[g],g)},drawChart:function(){e.AmXYChart.base.drawChart.call(this);var a=this.chartData;if(0<this.realWidth&&0<this.realHeight){e.ifArray(a)?(this.chartScrollbar&&
this.updateScrollbars(),this.zoomChart()):this.cleanChart();if(a=this.scrollbarH)this.hideXScrollbar?(a&&a.destroy(),this.scrollbarH=null):a.draw();if(a=this.scrollbarV)this.hideYScrollbar?(a.destroy(),this.scrollbarV=null):a.draw();this.zoomScrollbar();if(!this.autoMargins||this.marginsUpdated)this.dispDUpd(),this.chartCreated=!0}},cleanChart:function(){e.callMethod("destroy",[this.valueAxes,this.graphs,this.scrollbarV,this.scrollbarH,this.chartCursor])},zoomChart:function(){this.zoomObjects(this.valueAxes);
this.zoomObjects(this.graphs);this.zoomTrendLines();this.prevPlotAreaWidth=this.plotAreaWidth;this.prevPlotAreaHeight=this.plotAreaHeight},zoomObjects:function(a){var b=a.length,c,d;for(c=0;c<b;c++)d=a[c],d.zoom(0,this.chartData.length-1)},updateData:function(){this.parseData();var a=this.chartData,b=a.length-1,c=this.graphs,d=this.dataProvider,e=-Infinity,h=Infinity,f,k;if(d){for(f=0;f<c.length;f++)if(k=c[f],k.data=a,k.zoom(0,b),k=k.valueField){var m;for(m=0;m<d.length;m++)if(null!==l){var l=Number(d[m][k]);
l>e&&(e=l);l<h&&(h=l)}}isNaN(this.minValue)||(h=this.minValue);isNaN(this.maxValue)||(e=this.maxValue);for(f=0;f<c.length;f++)k=c[f],k.maxValue=e,k.minValue=h;if(a=this.chartCursor)a.type="crosshair",a.valueBalloonsEnabled=!1;this.dataChanged=!1;this.dispatchDataUpdated=!0}},processValueAxis:function(a){a.chart=this;a.minMaxField="H"==a.orientation?"x":"y";a.min=NaN;a.max=NaN},processGraph:function(a){e.isString(a.xAxis)&&(a.xAxis=this.getValueAxisById(a.xAxis));e.isString(a.yAxis)&&(a.yAxis=this.getValueAxisById(a.yAxis));
a.xAxis||(a.xAxis=this.xAxes[0]);a.yAxis||(a.yAxis=this.yAxes[0]);a.valueAxis=a.yAxis},parseData:function(){e.AmXYChart.base.parseData.call(this);this.chartData=[];var a=this.dataProvider,b=this.valueAxes,c=this.graphs,d;if(a)for(d=0;d<a.length;d++){var g={axes:{},x:{},y:{}},h=this.dataDateFormat,f=a[d],k;for(k=0;k<b.length;k++){var m=b[k].id;g.axes[m]={};g.axes[m].graphs={};var l;for(l=0;l<c.length;l++){var p=c[l],q=p.id;if(p.xAxis.id==m||p.yAxis.id==m){var r={};r.serialDataItem=g;r.index=d;var t=
{},n=f[p.valueField];null!==n&&(n=Number(n),isNaN(n)||(t.value=n));n=f[p.xField];null!==n&&("date"==p.xAxis.type&&(n=e.getDate(f[p.xField],h).getTime()),n=Number(n),isNaN(n)||(t.x=n));n=f[p.yField];null!==n&&("date"==p.yAxis.type&&(n=e.getDate(f[p.yField],h).getTime()),n=Number(n),isNaN(n)||(t.y=n));n=f[p.errorField];null!==n&&(n=Number(n),isNaN(n)||(t.error=n));r.values=t;this.processFields(p,r,f);r.serialDataItem=g;r.graph=p;g.axes[m].graphs[q]=r}}}this.chartData[d]=g}this.start=0;this.end=this.chartData.length-
1},formatString:function(a,b,c){var d=b.graph,g=d.numberFormatter;g||(g=this.nf);var h,f;"date"==b.graph.xAxis.type&&(h=e.formatDate(new Date(b.values.x),d.dateFormat,this),f=RegExp("\\[\\[x\\]\\]","g"),a=a.replace(f,h));"date"==b.graph.yAxis.type&&(h=e.formatDate(new Date(b.values.y),d.dateFormat,this),f=RegExp("\\[\\[y\\]\\]","g"),a=a.replace(f,h));a=e.formatValue(a,b.values,["value","x","y"],g);-1!=a.indexOf("[[")&&(a=e.formatDataContextValue(a,b.dataContext));return a=e.AmXYChart.base.formatString.call(this,
a,b,c)},addChartScrollbar:function(a){e.callMethod("destroy",[this.chartScrollbar,this.scrollbarH,this.scrollbarV]);if(a){this.chartScrollbar=a;this.scrollbarHeight=a.scrollbarHeight;var b="backgroundColor backgroundAlpha selectedBackgroundColor selectedBackgroundAlpha scrollDuration resizeEnabled hideResizeGrips scrollbarHeight updateOnReleaseOnly".split(" ");if(!this.hideYScrollbar){var c=new e.ChartScrollbar(this.theme);c.skipEvent=!0;c.chart=this;this.listenTo(c,"zoomed",this.handleScrollbarValueZoom);
e.copyProperties(a,c,b);c.rotate=!0;this.scrollbarV=c}this.hideXScrollbar||(c=new e.ChartScrollbar(this.theme),c.skipEvent=!0,c.chart=this,this.listenTo(c,"zoomed",this.handleScrollbarValueZoom),e.copyProperties(a,c,b),c.rotate=!1,this.scrollbarH=c)}},updateTrendLines:function(){var a=this.trendLines,b;for(b=0;b<a.length;b++){var c=a[b],c=e.processObject(c,e.TrendLine,this.theme);a[b]=c;c.chart=this;var d=c.valueAxis;e.isString(d)&&(c.valueAxis=this.getValueAxisById(d));d=c.valueAxisX;e.isString(d)&&
(c.valueAxisX=this.getValueAxisById(d));c.id||(c.id="trendLineAuto"+b+"_"+(new Date).getTime());c.valueAxis||(c.valueAxis=this.yAxes[0]);c.valueAxisX||(c.valueAxisX=this.xAxes[0])}},updateMargins:function(){e.AmXYChart.base.updateMargins.call(this);var a=this.scrollbarV;a&&(this.getScrollbarPosition(a,!0,this.yAxes[0].position),this.adjustMargins(a,!0));if(a=this.scrollbarH)this.getScrollbarPosition(a,!1,this.xAxes[0].position),this.adjustMargins(a,!1)},updateScrollbars:function(){e.AmXYChart.base.updateScrollbars.call(this);
var a=this.scrollbarV;a&&(this.updateChartScrollbar(a,!0),a.valueAxes=this.yAxes,a.gridAxis||(a.gridAxis=this.yAxes[0]));if(a=this.scrollbarH)this.updateChartScrollbar(a,!1),a.valueAxes=this.xAxes,a.gridAxis||(a.gridAxis=this.xAxes[0])},removeChartScrollbar:function(){e.callMethod("destroy",[this.scrollbarH,this.scrollbarV]);this.scrollbarV=this.scrollbarH=null},handleReleaseOutside:function(a){e.AmXYChart.base.handleReleaseOutside.call(this,a);e.callMethod("handleReleaseOutside",[this.scrollbarH,
this.scrollbarV])},update:function(){e.AmXYChart.base.update.call(this);this.scrollbarH&&this.scrollbarH.update&&this.scrollbarH.update();this.scrollbarV&&this.scrollbarV.update&&this.scrollbarV.update()},zoomScrollbar:function(){this.zoomValueScrollbar(this.scrollbarV);this.zoomValueScrollbar(this.scrollbarH)},handleCursorZoomReal:function(a,b,c,d){isNaN(a)||isNaN(b)||this.relativeZoomValueAxes(this.xAxes,a,b);isNaN(c)||isNaN(d)||this.relativeZoomValueAxes(this.yAxes,c,d);this.updateAfterValueZoom()},
handleCursorZoomStarted:function(){if(this.xAxes){var a=this.xAxes[0];this.startX0=a.relativeStart;this.endX0=a.relativeEnd;a.reversed&&(this.startX0=1-a.relativeEnd,this.endX0=1-a.relativeStart)}this.yAxes&&(a=this.yAxes[0],this.startY0=a.relativeStart,this.endY0=a.relativeEnd,a.reversed&&(this.startY0=1-a.relativeEnd,this.endY0=1-a.relativeStart))},updateChartCursor:function(){e.AmXYChart.base.updateChartCursor.call(this);var a=this.chartCursor;if(a){a.valueLineEnabled=!0;a.categoryLineAxis||(a.categoryLineAxis=
this.xAxes[0]);var b=this.valueAxis;if(a.valueLineBalloonEnabled){var c=a.categoryBalloonAlpha,d=a.categoryBalloonColor,g=a.color;void 0===d&&(d=a.cursorColor);for(var h=0;h<this.valueAxes.length;h++){var b=this.valueAxes[h],f=b.balloon;f||(f={});f=e.extend(f,this.balloon,!0);f.fillColor=d;f.balloonColor=d;f.fillAlpha=c;f.borderColor=d;f.color=g;b.balloon=f}}else for(c=0;c<this.valueAxes.length;c++)b=this.valueAxes[c],b.balloon&&(b.balloon=null);a.zoomable&&(this.hideYScrollbar||(a.vZoomEnabled=!0),
this.hideXScrollbar||(a.hZoomEnabled=!0))}},handleCursorPanning:function(a){var b=a.deltaX,c=a.delta2X,d;isNaN(c)&&(c=b,d=!0);var g=this.endX0,h=this.startX0,f=g-h,c=g-f*c,g=f;d||(g=0);b=e.fitToBounds(h-f*b,0,1-g);c=e.fitToBounds(c,g,1);this.relativeZoomValueAxes(this.xAxes,b,c);f=a.deltaY;a=a.delta2Y;isNaN(a)&&(a=f,d=!0);c=this.endY0;b=this.startY0;h=c-b;f=c+h*f;c=h;d||(c=0);d=e.fitToBounds(b+h*a,0,1-c);f=e.fitToBounds(f,c,1);this.relativeZoomValueAxes(this.yAxes,d,f);this.updateAfterValueZoom()},
handleValueAxisZoom:function(a){this.handleValueAxisZoomReal(a,"V"==a.valueAxis.orientation?this.yAxes:this.xAxes)},showZB:function(){var a,b=this.valueAxes;if(b)for(var c=0;c<b.length;c++){var d=b[c];0!==d.relativeStart&&(a=!0);1!=d.relativeEnd&&(a=!0)}e.AmXYChart.base.showZB.call(this,a)}})})();
