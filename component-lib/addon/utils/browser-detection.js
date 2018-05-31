/* Checks if the browser is IE or Edge */
export function ieEdgeDetection() {
  // Internet Explorer
  const isIE = false || !!document.documentMode;
  // Edge
  const isEdge = !isIE && !!window.StyleMedia;
  return isIE || isEdge;
}