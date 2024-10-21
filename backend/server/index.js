// server/index.js

require('dotenv').config();
const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const path = require('path');

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Serve static images
app.use('/captchas', express.static(path.join(__dirname, '../captch_images')));

// Database connection pool
const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// GET /api/captcha - Retrieve a random CAPTCHA
app.get('/api/captcha', async (req, res) => {
  try {
      const [rows] = await pool.query('SELECT * FROM captchas ORDER BY RAND() LIMIT 1');
      
      if (rows.length === 0) {
          return res.status(404).json({ message: 'No captchas found.' });
      }

      const captcha = rows[0];

      // Construct the URL for the captcha image
      const imageUrl = `${req.protocol}://${req.get('host')}/captchas/${captcha.image_filename}`;

      res.json({
          id: captcha.id,
          image_url: imageUrl
          // Note: Do NOT send the answer to the frontend for security reasons
      });
  } catch (error) {
      console.error('Error fetching captcha:', error);
      res.status(500).json({ message: 'Internal server error.' });
  }
});

// POST /api/validate-captcha - Validate user input
app.post('/api/validate-captcha', async (req, res) => {
  const { id, answer } = req.body;

  if (!id || !answer) {
      return res.status(400).json({ message: 'ID and answer are required.' });
  }

  try {
      const [rows] = await pool.query('SELECT * FROM captchas WHERE id = ?', [id]);

      if (rows.length === 0) {
          return res.status(404).json({ message: 'Captcha not found.' });
      }

      const captcha = rows[0];

      if (captcha.answer.toLowerCase() === answer.toLowerCase()) {
          return res.json({ success: true, message: 'Captcha validated successfully.' });
      } else {
          return res.json({ success: false, message: 'Incorrect captcha answer.' });
      }
  } catch (error) {
      console.error('Error validating captcha:', error);
      res.status(500).json({ message: 'Internal server error.' });
  }
});

// Start the server
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Captcha server is running on port ${PORT}`);
});