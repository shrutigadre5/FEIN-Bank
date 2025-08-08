function redirectTo(path) {
  document.body.classList.add('fade-out'); // Add the fade-out class
  setTimeout(() => {
    window.location.href = path; // Delay navigation slightly for transition
  }, 500);
}

window.addEventListener('DOMContentLoaded', () => {
  document.body.classList.add('fade-in'); // Trigger fade-in on page load
});


window.onload = () => {
  // Fade in when page loads
  document.querySelector(".fade-container").style.animation = "fadeIn 0.8s ease forwards";
};

// Optional fadeOut animation
const style = document.createElement('style');
style.innerHTML = `
@keyframes fadeOut {
  to {
    opacity: 0;
  }
}`;
document.head.appendChild(style);