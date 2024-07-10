/*
 FusionCharts JavaScript Library
 Copyright FusionCharts Technologies LLP
 License Information at <http://www.fusioncharts.com/license>

 @version 3.12.2
*/
(function(q){"object"===typeof module&&"undefined"!==typeof module.exports?module.exports=q:q(FusionCharts)})(function(q){q.register("module",["private","modules.renderer.js-ssgrid",function(){var J=this,p=J.hcLib,y=p.BLANKSTRING,g=p.pluck,d=p.pluckNumber,K=p.chartAPI,w=p.graphics.convertColor,x=p.getFirstColor,O=p.setLineHeight,D=Math,P=D.min,G=D.max,Q=D.ceil,R=D.round,S=p.toRaphaelColor,L=p.POSITION_START,M=p.HUNDREDSTRING,N=p.COLOR_TRANSPARENT;K("ssgrid",{standaloneInit:!0,creditLabel:!1,friendlyName:"ssgrid Chart",
defaultDatasetType:"ssgrid",canvasBorderThickness:1,singleseries:!0,bgColor:"#FFFFFF",bgAlpha:100,_drawCaption:function(){},_drawCanvas:function(){},_createAxes:function(){},_feedAxesRawData:function(){},_setCategories:function(){},_setAxisLimits:function(){},_spaceManager:function(){var e=this.components.dataset[0];e._manageSpace&&this._allocateSpace(e._manageSpace())}},K.sscartesian);q.register("component",["dataset","ssgrid",{init:function(e){var a=this.chart;if(!e)return!1;this.JSONData=e;this.chartGraphics=
a.chartGraphics;this.components={};this.config={};this.graphics={};this.visible=1===d(this.JSONData.visible,!Number(this.JSONData.initiallyhidden),1);this.configure()},configure:function(){var e=this.chart,a=this.config,b=e.jsonData.chart||{},l=e.components.colorManager;a.plotFillAngle=d(360-b.plotfillangle,e.isBar?180:90);a.plotFillAlpha=g(b.plotfillalpha,M);a.plotBorderAlpha=g(b.plotborderalpha,M);a.plotBorderColor=g(b.plotbordercolor,l.getColor("plotBorderColor"));a.plotDashLen=d(b.plotborderdashlen,
5);a.plotDashGap=d(b.plotborderdashgap,4);a.showPercentValues=d(b.showpercentvalues,0);a.numberItemsPerPage=d(b.numberitemsperpage)||void 0;a.showShadow=d(b.showshadow,0);a.baseFont=g(b.basefont,"Verdana,sans");a.baseFontSize=g(b.basefontsize,10)+"px";a.baseFontColor=x(g(b.basefontcolor,l.getColor("baseFontColor")));a.alternateRowBgColor=x(g(b.alternaterowbgcolor,l.getColor("altHGridColor")));a.alternateRowBgAlpha=g(b.alternaterowbgalpha,l.getColor("altHGridAlpha"))+y;a.listRowDividerThickness=d(b.listrowdividerthickness,
1);a.listRowDividerColor=x(g(b.listrowdividercolor,l.getColor("borderColor")));a.listRowDividerAlpha=d(d(b.listrowdivideralpha,l.getColor("altHGridAlpha")+15))+y;a.colorBoxWidth=d(b.colorboxwidth,8);a.colorBoxHeight=d(b.colorboxheight,8);a.navButtonRadius=d(b.navbuttonradius,7);a.navButtonColor=x(g(b.navbuttoncolor,l.getColor("canvasBorderColor")));a.navButtonHoverColor=x(g(b.navbuttonhovercolor,l.getColor("altHGridColor")));a.textVerticalPadding=d(b.textverticalpadding,3);a.navButtonPadding=d(b.navbuttonpadding,
5);a.colorBoxPadding=d(b.colorboxpadding,10);a.valueColumnPadding=d(b.valuecolumnpadding,10);a.nameColumnPadding=d(b.namecolumnpadding,5);a.shadow=d(b.showshadow,0)?{enabled:!0,opacity:a.plotFillAlpha/100}:!1;this.currentPage=0;this._setConfigure()},_setConfigure:function(){var e=this.chart,a=this.config,b=this.JSONData,l=e.jsonData&&e.jsonData.data,b=G(l&&l.length||0,b&&b.data&&b.data.length||0),n=e.components,e=n.colorManager,m=n.numberFormatter,k=a.plotColor=e.getPlotColor(this.index||this.positionIndex),
r=p.parseUnsafeString,c=a.plotBorderThickness,I=a.plotBorderDashStyle,h,f,T=p.getDashStyle,u=this.components.data,t,A,z,B,F=n=0,H;u||(u=this.components.data=[]);for(B=0;B<b&&l;B++)if(h=l[B])if(k=m.getCleanValue(h.value),f=r(g(h.label,h.name)),null!=k||f!=y)f=u[n]||(u[n]={config:{}}),f=f.config,f.tooltext=h.tooltext,f.showValue=d(h.showvalue,a.showValues),f.setValue=k=m.getCleanValue(h.value),f.setLink=g(h.link),f.toolTipValue=m.dataLabels(k),f.setDisplayValue=r(h.displayvalue),f.displayValue=m.dataLabels(k)||
y,f.dataLabel=r(g(h.label,h.name))||y,t=d(h.dashed),A=d(h.dashlen,void 0),z=d(h.dashgap,a.plotDashGap),F+=k,n+=1,f.plotBorderDashStyle=1===t?T(A,z,c):0===t?"none":I,k=g(h.color,e.getPlotColor(d(H-b,B))),g(h.ratio,a.plotFillRatio),t=g(h.alpha,a.plotFillAlpha),f.color=w(k,t),f.borderColor=w(a.plotBorderColor,g(h.alpha,a.plotBorderAlpha).toString()),H++;l={fontFamily:a.baseFont,fontSize:a.baseFontSize,color:a.baseFontColor};O(l);a.textStyle=l;a.actualDataLen=n;a.sumOfValues=F},_manageSpace:function(){var e=
this.chart,a=this.config,b=e.linkedItems.smartLabel,l=this.components.data,n=e.config,m=e.jsonData.chart||{},m=n.borderThickness=d(m.showborder,1)?d(m.borderthickness,1):0,k=n.height-2*m,m=n.width-2*m,r=a.textStyle,c=a.actualDataLen,I=a.sumOfValues,h=e.components.numberFormatter,e=0,f,g;b.useEllipsesOnOverflow(n.useEllipsesWhenOverflow);b.setStyle(r);g=l.length;for(n=0;n<g;n++)if(f=l[n])f=f&&f.config,a.showPercentValues&&(f.displayValue=h.percentValue(f.setValue/I*100)),f=b.getOriSize(f.displayValue),
e=G(e,f.width+a.valueColumnPadding);b=parseInt(r.lineHeight,10);b+=2*a.textVerticalPadding;b=G(b,a.colorBoxHeight+a.listRowDividerThickness);l=k/b;a.numberItemsPerPage&&l>=a.numberItemsPerPage?a.numberItemsPerPage>=c?(a.numberItemsPerPage=c,k/=a.numberItemsPerPage):(k-=2*(a.navButtonPadding+a.navButtonRadius),c=a.numberItemsPerPage,k/=c):(l>=c||(k-=2*(a.navButtonPadding+a.navButtonRadius),c=Math.floor(k/b)),k/=c);m=m-a.colorBoxPadding-a.colorBoxWidth-a.nameColumnPadding-e-a.valueColumnPadding;a.labelX=
a.colorBoxPadding+a.colorBoxWidth+a.nameColumnPadding;a.valueX=a.colorBoxPadding+a.colorBoxWidth+a.nameColumnPadding+m+a.valueColumnPadding;a.valueColumnPadding=a.valueColumnPadding;a.rowHeight=k;a.itemsPerPage=c;a.listRowDividerAttr={"stroke-width":a.listRowDividerThickness,stroke:w(a.listRowDividerColor,a.listRowDividerAlpha)};a.alternateRowColor=w(a.alternateRowBgColor,a.alternateRowBgAlpha);return{top:0,bottom:0}},draw:function(){var e=this.config,a=this.chart,b=a.linkedItems.smartLabel,l=a.components.paper,
n=a.graphics.datasetGroup,m=this.components.data,k=a.jsonData&&a.jsonData.data,k=k&&k.length||0,d=G(k,m.length),c=this.graphics,g,c=a.config,h=c.borderThickness,f=h,a=a.config.width-c.borderThickness,p=S(e.alternateRowColor),u=e.rowHeight,t=e.listRowDividerAttr,A=t["stroke-width"]%2/2,z=e.colorBoxPadding+h,B=e.colorBoxHeight,F=e.colorBoxWidth,H=e.labelX+h,U=e.valueX+h,q=e.textStyle,y=e.itemsPerPage,C=0,x=this.currentPage||(this.currentPage=0),w={},D,E,v;this.graphics.container||(this.graphics.container=
[]);this.currentPage=x=P(Q(d/y)-1,x);for(E=0;E<d;E++)if(1!=(E+1)%y&&1!=y&&g||(f=h,(g=this.graphics.container[C])||(g=this.graphics.container[C]=l.group("grid-"+C,n)),C!==x?g.hide():g.show(),C+=1,w={pageId:C,data:[]},g.data("eventArgs",w)),c=m[E])v=c&&c.config,c=c&&(c.graphics||(c.graphics={})),E>=k?(c.alternateRow&&c.alternateRow.remove(),c.alternateRow=void 0,c.listRowDivideElem&&c.listRowDivideElem.remove(),c.listRowDivideElem=void 0,c.element&&c.element.remove(),c.element=void 0,c.label&&c.label.remove(),
c.label=void 0,c.displayValue&&c.displayValue.remove(),c.displayValue=void 0,c.listRowDivideElem&&c.listRowDivideElem.remove(),c.listRowDivideElem=void 0):(0===E%2&&(c.alternateRow||(c.alternateRow=l.rect()),g.appendChild(c.alternateRow),c.alternateRow.attr({x:h,y:f+.5*e.listRowDividerThickness,width:a-h,height:u,fill:p,"stroke-width":0})),c.element||(c.element=l.rect()),g.appendChild(c.element),c.element.attr({x:z,y:f+u/2-B/2,width:F,height:B,fill:v.color,"stroke-width":0,stroke:"#000000"}).shadow(e.shadow),
D=b.getSmartText(v.displayValue).width||0,c.displayValue||(c.displayValue=l.text()),g.appendChild(c.displayValue),c.displayValue.attr({text:v.displayValue,x:U,y:f+u/2,fill:q.color,direction:e.textDirection,"text-anchor":L}).css(q),c.label||(c.label=l.text()),v.label=b.getSmartText(v.dataLabel,a-(D+F+z),u),g.appendChild(c.label),c.label.attr({text:v.label.text,x:H,y:f+u/2,fill:q.color,direction:e.textDirection,"text-anchor":L}).css(q),w.data.push({color:v.color,displayValue:v.displayValue,label:v.dataLabel,
originalText:v.label.text,y:f+u/2}),f+=u,v=R(f)+A,c.listRowDivideElem||(c.listRowDivideElem=l.path()),g.appendChild(c.listRowDivideElem),c.listRowDivideElem.attr("path",["M",h,v,"L",a,v]).attr(t));for(d=this.graphics.container.length-1;d>=C;--d)g=this.graphics.container,g[d].remove(),g.splice(d,1);this._drawSSGridNavButton()},_drawSSGridNavButton:function(){var e=this,a=e.chart,b=e.config,l=b.actualDataLen,g=b.itemsPerPage,d=e.graphics,k=a.components.paper,r=a.config.borderThickness,c=b.navButtonColor,
p=b.navButtonHoverColor,h=b.navButtonRadius,f=.67*h,b=r+b.navButtonPadding+f+b.itemsPerPage*b.rowHeight+.5*h,r=20+r,q=a.config.width-r,a=a.graphics,u=a.trackerGroup,t=a.pageNavigationLayer,A=a.pagePreNavigationLayer,z=a.pageNextNavigationLayer,B=d.container.length,y=e.currentPage,x,w;t||(t=a.pageNavigationLayer=k.group("page-nav",u));A||(A=a.pagePreNavigationLayer=k.group("page-prev-nav",t));z||(z=a.pageNextNavigationLayer=k.group("page-next-nav",t));l>g?(t.show(),d.navElePrv||(d.navElePrv=k.path(A)),
x=d.navElePrv.attr({path:["M",r,b,"L",r+h+f,b-f,r+h,b,r+h+f,b+f,"Z"],fill:c,"stroke-width":0,cursor:"pointer"}),d.navTrackerPrv||(d.navTrackerPrv=k.circle(A).mouseover(function(){x.attr({fill:p,cursor:"pointer"})}).mouseout(function(){x.attr({fill:c})}).click(function(){e._nenagitePage(-1)})),d.navTrackerPrv.attr({cx:r+h,cy:b,r:h,fill:N,"stroke-width":0,cursor:"pointer"}),d.navEleNxt||(w=d.navEleNxt=k.path(z)),w=d.navEleNxt.attr({path:["M",q,b,"L",q-h-f,b-f,q-h,b,q-h-f,b+f,"Z"],fill:c,"stroke-width":0,
cursor:"pointer"}),d.navTrackerNxt||(d.navTrackerNxt=k.circle(z).mouseover(function(){w.attr({fill:p})}).mouseout(function(){w.attr({fill:c})}).click(function(){e._nenagitePage(1)})),d.navTrackerNxt.attr({cx:q-h,cy:b,r:h,fill:N,"stroke-width":0,cursor:"pointer"}),0===y?A.hide():A.show(),y===B-1?z.hide():z.show()):t.hide()},_nenagitePage:function(e){var a=this.chart,b=this.graphics.container,d=this.currentPage,g=a.graphics,m=g.pagePreNavigationLayer,g=g.pageNextNavigationLayer,k=b.length;b[d+e]&&(b[d].hide(),
b[d+e].show(),d=this.currentPage+=e);e=b[d].data("eventArgs");J.raiseEvent("pageNavigated",{pageId:d,data:e.data},a.chartInstance);0===d?m.hide():m.show();d===k-1?g.hide():g.show()}}])},[3,2,0,"sr2"]])});

//# sourceMappingURL=http://localhost:3052/3.12.2/map/licensed/fusioncharts.ssgrid.js.map
