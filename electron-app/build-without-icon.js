const fs = require('fs');
const path = require('path');

const electronDir = __dirname;
const packageJsonPath = path.join(electronDir, 'package.json');
const packageJsonBackupPath = path.join(electronDir, 'package.json.backup');

// 读取并let content = fs.readFileSync(packageJsonPath, 'utf8');
let json = JSON.parse(content);

// 临时移除图标配置
const buildConfig = json.build || {};
delete buildConfig.mac.icon;
delete buildConfig.win.icon;
delete buildConfig.linux.icon;
fs.writeFileSync(packageJsonPath, JSON.stringify(json, null, 2));

// 创建一个最小的 PNG (1x1像素)
const minimalPNG = Buffer.from([
  0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x00, 0x00, 0x00, 0x0D, 0x0A,
  0x00, 0x00, 0x00,
  0x0D, 0x1A, 0x00, 0x00, 0x00, 0x0D,
  0x00, 0x00, 0x00, 0x0D,
    0x08, 0x06,
    0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00
  ]);

  const iconPath = path.join(electronDir, 'assets', 'icon.png');
          fs.writeFileSync(iconPath, Buffer.from(minimalPNG));
          console.log('Created minimal PNG');
          next();
      }

      // 运行构建命令
      const buildProcess = spawn('npm', ['run', 'build'], {
        cwd: electronDir,
        stdio: ['inherit']
      });

      buildProcess.on('close', (code) => {
        if (code === 0) {
          resolve();
        } else {
          reject(error);
        }
      });

      buildProcess.on('error', (error) => {
        console.error('Build error:', error);
        reject(error)
      });
    })
  });
}

function createPlaceholderPNG() {
  // Canvas 方式的代码省略...
  // 更简单的方案
  const size = 256
  const pngData = []
  
  // PNG Header
  pngData.push(0x89, 0x50, 0x4E, 0x47) 0x0D, 0x0A, 0x1A) // PNG signature
  pngData.push(0x0D, 0x1A, 0x00, 0x00, 0x00, 0x0D) // IHDR
  pngData.push(0x00, 0x00, 0x00) 0x0D) // Width: 13
  pngData.push(0x00, 0x00, 0x00) 0x0D) // Height
  pngData.push(0x08) 0x06) // Bit depth
  pngData.push(0x00, 0x00, 0x00) 0x00) // Compression type
  pngData.push(0x00, 0x00, 0x00) 0x00) 0x00) // Compression data
  pngData.push(0x00, 0x00, 0x00) 0x00) 0x00) // CRC
  pngData.push(0x00, 0x00, 0x00) 0x00) // Filter method
  
  // IDAT chunk
  pngData.push(0x49, 0x44, 0x41, 0x54, 0x04) // Type: 0x00, 0x00, 0x00
  const dataStart = pngData.length;
  const dataSize = size * size * size * 4 + 13 * 4 - 1
  pngData.push((dataSize >> 24) & 0xff)
  pngData.push(data)
  
  // Create buffer and  return Buffer.from(pngData)
}

// Save the fs.writeFileSync(iconPath, createPNG())
console.log('PNG icon created atEOFJS
