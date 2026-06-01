// ==================== PLAYER TABS ====================
function switchPlayer(type, btn) {
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
            if (trailerVideo) { trailerVideo.load(); trailerVideo.play(); }
        }
    }

    tabs.forEach(tab => tab.classList.remove('active'));
    if (btn) btn.classList.add('active');
}

// ==================== GENRE MANAGEMENT ====================

// Читаем CSRF токен из мета-тегов страницы
function getCsrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    if (token && header) {
        return { [header]: token, 'Content-Type': 'application/json' };
    }
    return { 'Content-Type': 'application/json' };
}

async function addGenre() {
    const input = document.getElementById('newGenreInput');
    const errorEl = document.getElementById('genreError');
    const name = input.value.trim();

    errorEl.style.display = 'none';

    if (!name) {
        showGenreError('Введите название жанра');
        return;
    }

    try {
        const res = await fetch('/admin/genres', {
            method: 'POST',
            headers: getCsrfHeaders(),
            body: JSON.stringify({ name })
        });

        const data = await res.json();

        if (!res.ok) {
            showGenreError(data.error || 'Ошибка при добавлении');
            return;
        }

        appendGenreItem(data.id, data.name, true);
        input.value = '';

    } catch (e) {
        showGenreError('Ошибка соединения с сервером');
    }
}

async function deleteGenre(btn) {
    const id = btn.dataset.id;
    if (!confirm('Удалить этот жанр из базы данных?')) return;

    try {
        const res = await fetch('/admin/genres/' + id, {
            method: 'DELETE',
            headers: getCsrfHeaders()
        });

        if (res.ok) {
            document.getElementById('genre-item-' + id).remove();
        } else {
            const data = await res.json();
            alert(data.error || 'Не удалось удалить жанр');
        }
    } catch (e) {
        alert('Ошибка соединения с сервером');
    }
}

function appendGenreItem(id, name, checked) {
    const grid = document.getElementById('genreGrid');

    const wrapper = document.createElement('div');
    wrapper.className = 'genre-item';
    wrapper.dataset.id = id;
    wrapper.id = 'genre-item-' + id;

    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.id = 'genre_' + id;
    checkbox.name = 'genres';
    checkbox.value = name;
    checkbox.checked = checked;

    const label = document.createElement('label');
    label.htmlFor = 'genre_' + id;
    label.textContent = name;

    const delBtn = document.createElement('button');
    delBtn.type = 'button';
    delBtn.className = 'genre-delete-btn';
    delBtn.dataset.id = id;
    delBtn.title = 'Удалить ' + name;
    delBtn.textContent = '×';
    delBtn.onclick = function () { deleteGenre(this); };

    wrapper.appendChild(checkbox);
    wrapper.appendChild(label);
    wrapper.appendChild(delBtn);
    grid.appendChild(wrapper);
}

function showGenreError(msg) {
    const el = document.getElementById('genreError');
    el.textContent = msg;
    el.style.display = 'block';
}

// ==================== DOM READY ====================
document.addEventListener('DOMContentLoaded', () => {

    // ── Star Rating ──────────────────────────────────────────────────────────
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

    // ── Auto-hide alerts ─────────────────────────────────────────────────────
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });

    // ── Navbar scroll shadow ─────────────────────────────────────────────────
    const navbar = document.querySelector('.navbar');
    if (navbar) {
        window.addEventListener('scroll', () => {
            navbar.style.boxShadow = window.scrollY > 20
                ? '0 4px 20px rgba(0,0,0,0.4)'
                : 'none';
        });
    }

    // ── Genre input: Enter → addGenre ────────────────────────────────────────
    // Проверяем наличие элемента — этот код работает только на странице с формой
    const newGenreInput = document.getElementById('newGenreInput');
    if (newGenreInput) {
        newGenreInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                addGenre();
            }
        });
    }

}); // конец DOMContentLoaded