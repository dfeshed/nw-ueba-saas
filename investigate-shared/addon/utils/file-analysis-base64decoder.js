export function base64ToUnicode(str) {
  return decodeURIComponent(atob(str).split('').map(function(c) {
    const convertedSubstring = `00${c.charCodeAt(0).toString(16)}`.slice(-2);
    return `%${convertedSubstring}`;
  }).join(''));
}