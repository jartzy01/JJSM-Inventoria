const functions = require("firebase-functions");
const admin = require("firebase-admin");
const sgMail = require("@sendgrid/mail");

// ✅ Initialize Firebase Admin
admin.initializeApp();

// 🔐 Set your SendGrid API Key from Firebase functions config
const SENDGRID_API_KEY = functions.config().sendgrid.key;
sgMail.setApiKey(SENDGRID_API_KEY);

// ✅ 1st-Gen Callable Function
exports.sendEmail = functions.https.onCall(async (data, context) => {
  const { to, subject, text } = data;

  // 🚫 Validate required fields
  if (!to || !subject || !text) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "Missing required fields"
    );
  }

  const msg = {
    to,
    from: "mohammad.khajavi.2017@gmail.com",
    subject,
    text,
  };

  try {
    await sgMail.send(msg);
    return { success: true };
  } catch (error) {
    console.error("SendGrid Error:", error);
    throw new functions.https.HttpsError("internal", "Failed to send email");
  }
});
