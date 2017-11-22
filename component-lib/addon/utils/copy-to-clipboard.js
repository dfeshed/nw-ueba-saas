/**
 * Utility function to copy a text to clipboard
 * @param text Value to be copied
 * @public
 */
export default function copyToClipboard(text) {
  // The best way to do this is to create a temporary
  // textarea, set it's value to the selected text, and then "copy" it.
  const fakeEl = document.createElement('textarea');
  try {
    fakeEl.textContent = text;
    fakeEl.style.position = 'fixed';  // Prevent scrolling to bottom of page in MS Edge.
    document.body.appendChild(fakeEl);
    fakeEl.select();
    return document.execCommand('copy');  // Security exception may be thrown by some browsers.
  } catch (ex) {
    return false;
  } finally {
    document.body.removeChild(fakeEl);
  }
}