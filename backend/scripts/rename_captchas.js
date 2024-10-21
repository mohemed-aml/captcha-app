const fs = require('fs');
const path = require('path');
const { v4: uuidv4 } = require('uuid');

// Directory containing CAPTCHA images
const CAPTCHAS_DIR = path.join(__dirname, '../captcha_images');

// Path to the mapping file
const MAPPING_FILE = path.join(__dirname, 'captcha_mapping.json');

// Read existing mappings
let mappings = [];
if (fs.existsSync(MAPPING_FILE)) {
    const data = fs.readFileSync(MAPPING_FILE, 'utf-8');
    mappings = JSON.parse(data);
}

fs.readdir(CAPTCHAS_DIR, (err, files) => {
    if (err) {
        console.error('Error reading captcha_images directory:', err);
        return;
    }

    files.forEach(file => {
        const ext = path.extname(file).toLowerCase();
        if (ext === '.png' || ext === '.jpg' || ext === '.jpeg') {
            const answer = path.basename(file, ext); // Extract answer from filename
            const newFileName = `${uuidv4()}${ext}`; // Generate new random filename
            const oldPath = path.join(CAPTCHAS_DIR, file);
            const newPath = path.join(CAPTCHAS_DIR, newFileName);

            // Rename the file
            fs.rename(oldPath, newPath, (err) => {
                if (err) {
                    console.error(`Error renaming file ${file}:`, err);
                } else {
                    console.log(`Renamed ${file} to ${newFileName}`);
                    // Update the mappings
                    mappings.push({ image_filename: newFileName, answer: answer });
                    // Write updated mappings to the mapping file
                    fs.writeFileSync(MAPPING_FILE, JSON.stringify(mappings, null, 2), 'utf-8');
                }
            });
        }
    });
});