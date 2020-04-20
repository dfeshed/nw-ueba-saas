const SCRIPT_FILES = ['cmd.exe', 'powershell.exe', 'wscript.exe', 'cscript.exe', 'rundll32.exe'];

// script file may come as filename.src. Make sure to ignore them, when multiple filename.src is available
export const getSrcFilename = (metas) => {
  const metaPairs = metas.filter((d) => d[0] === 'filename.src');
  let srcFilename = '';
  if (metaPairs.length === 1) {
    srcFilename = metaPairs[0][1];
  } else if (metaPairs.length > 1) {
    srcFilename = metaPairs.filter((d) => !SCRIPT_FILES.includes(d[1]))[0][1];
  }
  return srcFilename;
};