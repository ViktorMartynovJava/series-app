// ==================== PLAYER TABS ====================
function switchPlayer(type) {
    const videoPlayer = document.getElementById('video-player');
    const trailerPlayer = document.getElementById('trailer-player');
    const tabs = document.querySelectorAll('.player-tab');

    if (type === 'video') {
        if (videoPlayer) videoPlayer.style.display = 'block';
        if (trailerPlayer) {
            trailerPlayer.style.display = 'none';

            const trailerVideo = trailerPlayer.querySelector('video');
            if (trailerVideo) trailerVideo.pause();
        }
    } else if (type === 'trailer') {
        if (videoPlayer) {
            videoPlayer.style.display = 'none';

            const videoElement = videoPlayer.querySelector('video');
            if (videoElement) videoElement.pause();
        }
        if (trailerPlayer) {
            trailerPlayer.style.display = 'block';

            const trailerVideo = trailerPlayer.querySelector('video');
            if (trailerVideo) {
                trailerVideo.load();
                trailerVideo.play();
            }
        }
    }


    tabs.forEach(tab => tab.classList.remove('active'));
    if (window.event && window.event.target) {
        window.event.target.classList.add('active');
    }
}

// ==================== STAR RATING ====================
document.addEventListener('DOMContentLoaded', () => {
    const stars = document.querySelectorAll('.star');
    const ratingInput = document.getElementById('ratingInput');

    if (stars.length && ratingInput) {
        stars.forEach(star => {
            star.addEventListener('click', () => {
                const val = parseInt(star.dataset.value);
                ratingInput.value = val;
                stars.forEach(s => {
                    s.classList.toggle('active', parseInt(s.dataset.value) <= val);
                });
            });

            star.addEventListener('mouseenter', () => {
                const val = parseInt(star.dataset.value);
                stars.forEach(s => {
                    s.style.opacity = parseInt(s.dataset.value) <= val ? '1' : '0.4';
                });
            });

            star.addEventListener('mouseleave', () => {
                stars.forEach(s => s.style.opacity = '1');
            });
        });
    }

    // ==================== AUTO-HIDE ALERTS ====================
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });

    // ==================== NAVBAR SCROLL ====================
    const navbar = document.querySelector('.navbar');
    if (navbar) {
        window.addEventListener('scroll', () => {
            navbar.style.boxShadow = window.scrollY > 20
                ? '0 4px 20px rgba(0,0,0,0.4)'
                : 'none';
        });
    }
});