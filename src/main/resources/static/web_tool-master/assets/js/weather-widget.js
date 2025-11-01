(function () {
    'use strict';

    const container = document.getElementById('he-plugin-simple');
    if (!container) {
        return;
    }

    if (container.dataset.weatherEnhanced === '1') {
        return;
    }
    container.dataset.weatherEnhanced = '1';

    const config = (window.WIDGET && window.WIDGET.CONFIG) || {};
    const accentColor = normaliseColor(config.tmpColor) || '#E4C600';
    const fallbackLocation = {
        latitude: parseFloat(config.defaultLat) || 39.9042,
        longitude: parseFloat(config.defaultLon) || 116.4074,
        name: config.defaultCity || '北京'
    };
    const loadingText = config.loadingText || '天气加载中...';
    const errorText = config.errorText || '天气信息暂不可用';

    let refreshMinutes = parseInt(config.refreshMinutes, 10);
    if (isNaN(refreshMinutes)) {
        refreshMinutes = 30;
    } else if (refreshMinutes > 0 && refreshMinutes < 10) {
        refreshMinutes = 10;
    }
    const refreshInterval = refreshMinutes > 0 ? refreshMinutes * 60 * 1000 : 0;

    let hasRendered = false;
    let cachedLocation = null;

    container.classList.add('weather-widget');
    container.style.setProperty('--weather-accent', accentColor);
    container.setAttribute('aria-live', 'polite');

    renderLoading();

    const WEATHER_CODE_TEXT = {
        0: '晴',
        1: '多云',
        2: '多云',
        3: '阴',
        45: '雾',
        48: '雾',
        51: '毛毛雨',
        53: '毛毛雨',
        55: '毛毛雨',
        56: '冻雨',
        57: '冻雨',
        61: '小雨',
        63: '中雨',
        65: '大雨',
        66: '冻雨',
        67: '冻雨',
        71: '小雪',
        73: '中雪',
        75: '大雪',
        77: '雪粒',
        80: '阵雨',
        81: '阵雨',
        82: '强阵雨',
        85: '阵雪',
        86: '阵雪',
        95: '雷雨',
        96: '雷雨伴冰雹',
        99: '雷雨伴冰雹'
    };

    function normaliseColor(color) {
        if (!color) {
            return null;
        }
        const trimmed = String(color).trim();
        if (!trimmed) {
            return null;
        }
        if (trimmed.startsWith('#')) {
            return trimmed;
        }
        if (/^[0-9a-fA-F]{3}$/.test(trimmed) || /^[0-9a-fA-F]{6}$/.test(trimmed)) {
            return `#${trimmed}`;
        }
        return trimmed;
    }

    function cloneLocation(location) {
        return {
            latitude: location.latitude,
            longitude: location.longitude,
            name: location.name
        };
    }

    function renderLoading() {
        container.innerHTML = `<span class="weather-loading">${loadingText}</span>`;
        container.dataset.weatherStatus = 'loading';
    }

    function renderError() {
        container.innerHTML = `<span class="weather-error">${errorText}</span>`;
        container.dataset.weatherStatus = 'error';
    }

    function renderWeather({ temperature, description, city, humidity }) {
        const displayCity = city || fallbackLocation.name || '当前位置';
        const hasTemperature = typeof temperature === 'number' && !isNaN(temperature);
        const temperatureText = hasTemperature ? `${Math.round(temperature)}°C` : '--';
        const hasHumidity = typeof humidity === 'number' && !isNaN(humidity);
        const humidityText = hasHumidity ? ` · 湿度 ${Math.round(humidity)}%` : '';
        const descText = description || '天气';

        container.innerHTML = `
            <div class="weather-main">
                <span class="weather-temp">${temperatureText}</span>
                <div class="weather-meta">
                    <span class="weather-city">${displayCity}</span>
                    <span class="weather-desc">${descText}${humidityText}</span>
                </div>
            </div>
        `;
        container.dataset.weatherStatus = 'ready';
        hasRendered = true;
    }

    function describeWeather(code) {
        const numericCode = Number(code);
        if (isNaN(numericCode)) {
            return '天气';
        }
        if (WEATHER_CODE_TEXT[numericCode]) {
            return WEATHER_CODE_TEXT[numericCode];
        }
        if (numericCode >= 51 && numericCode <= 57) {
            return '毛毛雨';
        }
        if (numericCode >= 61 && numericCode <= 67) {
            return '降雨';
        }
        if (numericCode >= 71 && numericCode <= 77) {
            return '降雪';
        }
        if (numericCode >= 80 && numericCode <= 86) {
            return '阵雨';
        }
        if (numericCode >= 95 && numericCode <= 99) {
            return '雷雨';
        }
        return '天气';
    }

    function getLocation() {
        if (cachedLocation) {
            return Promise.resolve(cloneLocation(cachedLocation));
        }

        return new Promise((resolve) => {
            if (!navigator.geolocation) {
                resolve(cloneLocation(fallbackLocation));
                return;
            }

            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const coords = position && position.coords ? position.coords : {};
                    if (typeof coords.latitude === 'number' && typeof coords.longitude === 'number') {
                        resolve({
                            latitude: coords.latitude,
                            longitude: coords.longitude
                        });
                    } else {
                        resolve(cloneLocation(fallbackLocation));
                    }
                },
                () => resolve(cloneLocation(fallbackLocation)),
                { maximumAge: 30 * 60 * 1000, timeout: 4500 }
            );
        }).then((location) => {
            if (!location.name && location.latitude === fallbackLocation.latitude && location.longitude === fallbackLocation.longitude) {
                location.name = fallbackLocation.name;
            }
            return location;
        });
    }

    function reverseGeocode(location) {
        if (location.name) {
            cachedLocation = cloneLocation(location);
            return Promise.resolve(location);
        }

        if (typeof window.fetch !== 'function') {
            location.name = fallbackLocation.name;
            cachedLocation = cloneLocation(location);
            return Promise.resolve(location);
        }

        const params = new URLSearchParams({
            latitude: location.latitude,
            longitude: location.longitude,
            language: 'zh',
            count: '1'
        });

        return fetch(`https://geocoding-api.open-meteo.com/v1/reverse?${params.toString()}`)
            .then((response) => (response.ok ? response.json() : null))
            .then((data) => {
                if (data && Array.isArray(data.results) && data.results.length) {
                    const result = data.results[0];
                    location.name = result.name || result.admin2 || result.admin1 || fallbackLocation.name;
                } else {
                    location.name = fallbackLocation.name;
                }
                return location;
            })
            .catch(() => {
                location.name = fallbackLocation.name;
                return location;
            })
            .then((loc) => {
                cachedLocation = cloneLocation(loc);
                return loc;
            });
    }

    function fetchWeather(location) {
        if (typeof window.fetch !== 'function') {
            return Promise.reject(new Error('fetch not supported'));
        }

        const params = new URLSearchParams({
            latitude: location.latitude,
            longitude: location.longitude,
            current: 'temperature_2m,relative_humidity_2m,weather_code',
            timezone: 'auto'
        });

        return fetch(`https://api.open-meteo.com/v1/forecast?${params.toString()}`, { cache: 'no-store' })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`Weather request failed with status ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                if (!data || !data.current) {
                    throw new Error('Invalid weather response');
                }
                return {
                    temperature: data.current.temperature_2m,
                    humidity: data.current.relative_humidity_2m,
                    code: data.current.weather_code
                };
            });
    }

    function updateWeather() {
        if (typeof window.fetch !== 'function') {
            if (!hasRendered) {
                renderError();
            }
            return;
        }

        if (!hasRendered) {
            renderLoading();
        }

        getLocation()
            .then((location) => reverseGeocode(location)
                .then((resolvedLocation) => fetchWeather(resolvedLocation).then((weather) => ({
                    location: resolvedLocation,
                    weather
                }))))
            .then(({ location, weather }) => {
                renderWeather({
                    temperature: weather.temperature,
                    humidity: weather.humidity,
                    description: describeWeather(weather.code),
                    city: location.name
                });
            })
            .catch((error) => {
                if (!hasRendered) {
                    renderError();
                }
                console.warn('[weather-widget] 无法获取天气信息', error);
            });
    }

    updateWeather();

    if (refreshInterval > 0) {
        setInterval(updateWeather, refreshInterval);
    }
})();
