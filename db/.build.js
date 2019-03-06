const childproc = require('child_process');
const fs = require('fs');
const os = require('os');

if (fs.existsSync('../package.json')) { // true at build-time, false at CF staging time
    childproc.execSync('npm install  && npm run build', { cwd: '..', shell: (os.platform() === 'win32'), stdio: 'inherit' })
}
