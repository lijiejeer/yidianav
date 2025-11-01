const API_BASE = '/api';
let currentCategoryId = null;
let currentCardId = null;
let currentAdId = null;
let currentFriendLinkId = null;
let categories = [];

const notificationContainer = document.getElementById('notificationContainer');

function showMessage(message, type = 'success', timeout = 3200) {
    if (!notificationContainer || !message) {
        return;
    }

    while (notificationContainer.childElementCount >= 3) {
        notificationContainer.removeChild(notificationContainer.firstChild);
    }

    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.setAttribute('role', 'alert');
    alert.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert" aria-label="关闭">
            <span aria-hidden="true">&times;</span>
        </button>
    `;

    notificationContainer.appendChild(alert);

    if (timeout) {
        setTimeout(() => {
            if (window.jQuery && $(alert).length) {
                $(alert).alert('close');
            } else if (alert.parentNode) {
                alert.parentNode.removeChild(alert);
            }
        }, timeout);
    }
}

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/admin-login.html';
        return false;
    }
    return true;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = '/admin-login.html';
}

if (!checkAuth()) {
    throw new Error('Not authenticated');
}

document.getElementById('usernameDisplay').textContent = localStorage.getItem('username') || 'Admin';

document.querySelectorAll('.menu-link').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        const section = this.getAttribute('data-section');
        
        document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
        document.querySelectorAll('.menu-link').forEach(l => l.classList.remove('active'));
        
        document.getElementById(section).classList.add('active');
        this.classList.add('active');
        
        switch(section) {
            case 'dashboard':
                loadDashboard();
                break;
            case 'categories':
                loadCategories();
                break;
            case 'cards':
                loadCards();
                break;
            case 'ads':
                loadAds();
                break;
            case 'friendlinks':
                loadFriendLinks();
                break;
            case 'backup':
                loadBackups();
                break;
        }
    });
});

function loadDashboard() {
    fetch(`${API_BASE}/admin/categories`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('statCategories').textContent = result.data.length;
            }
        });

    fetch(`${API_BASE}/admin/cards`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('statCards').textContent = result.data.length;
            }
        });

    fetch(`${API_BASE}/admin/friendlinks`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('statLinks').textContent = result.data.length;
            }
        });

    fetch(`${API_BASE}/admin/users/login-history`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                const tbody = document.getElementById('loginHistoryTable');
                tbody.innerHTML = result.data.map(h => `
                    <tr>
                        <td>${new Date(h.loginTime).toLocaleString()}</td>
                        <td>${h.loginIp}</td>
                        <td>${h.userAgent ? h.userAgent.substring(0, 50) + '...' : '-'}</td>
                    </tr>
                `).join('');
            }
        });
}

function loadCategories() {
    fetch(`${API_BASE}/admin/categories`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                categories = result.data;
                const tbody = document.getElementById('categoriesTable');
                tbody.innerHTML = result.data.map(c => `
                    <tr>
                        <td>${c.id}</td>
                        <td><i class="${c.icon}"></i> ${c.name}</td>
                        <td><code>${c.icon || '-'}</code></td>
                        <td>${c.sortOrder}</td>
                        <td>
                            <button class="btn btn-sm btn-info" onclick="editCategory(${c.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="deleteCategory(${c.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

function showCategoryModal(id = null) {
    currentCategoryId = id;
    if (id) {
        const category = categories.find(c => c.id === id);
        if (category) {
            document.getElementById('categoryId').value = category.id;
            document.getElementById('categoryName').value = category.name;
            document.getElementById('categoryIcon').value = category.icon || '';
            document.getElementById('categorySort').value = category.sortOrder;
        }
    } else {
        document.getElementById('categoryForm').reset();
        document.getElementById('categoryId').value = '';
    }
    $('#categoryModal').modal('show');
}

function editCategory(id) {
    fetch(`${API_BASE}/admin/categories/${id}`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('categoryId').value = result.data.id;
                document.getElementById('categoryName').value = result.data.name;
                document.getElementById('categoryIcon').value = result.data.icon || '';
                document.getElementById('categorySort').value = result.data.sortOrder;
                currentCategoryId = id;
                $('#categoryModal').modal('show');
            }
        });
}

function saveCategory() {
    const id = document.getElementById('categoryId').value;
    const data = {
        name: document.getElementById('categoryName').value,
        icon: document.getElementById('categoryIcon').value,
        sortOrder: parseInt(document.getElementById('categorySort').value)
    };

    const url = id ? `${API_BASE}/admin/categories/${id}` : `${API_BASE}/admin/categories`;
    const method = id ? 'PUT' : 'POST';

    fetch(url, {
        method,
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            $('#categoryModal').modal('hide');
            showMessage(result.message || '栏目保存成功');
            loadCategories();
        } else {
            showMessage(result.message || '栏目保存失败', 'danger');
        }
    })
    .catch(() => showMessage('保存栏目时出现异常，请稍后再试。', 'danger'));
}

function deleteCategory(id) {
    if (!confirm('确定要删除这个栏目吗？')) return;

    fetch(`${API_BASE}/admin/categories/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage(result.message || '栏目删除成功');
            loadCategories();
        } else {
            showMessage(result.message || '栏目删除失败', 'danger');
        }
    })
    .catch(() => showMessage('删除栏目时出现异常，请稍后再试。', 'danger'));
}

function loadCards() {
    Promise.all([
        fetch(`${API_BASE}/admin/cards`, { headers: getAuthHeaders() }),
        fetch(`${API_BASE}/admin/categories`, { headers: getAuthHeaders() })
    ])
    .then(([cardsRes, catsRes]) => Promise.all([cardsRes.json(), catsRes.json()]))
    .then(([cardsResult, catsResult]) => {
        if (cardsResult.success && catsResult.success) {
            categories = catsResult.data;
            const tbody = document.getElementById('cardsTable');
            tbody.innerHTML = cardsResult.data.map(card => {
                const cat = categories.find(c => c.id === card.categoryId);
                return `
                    <tr>
                        <td>${card.id}</td>
                        <td>${card.name}</td>
                        <td><a href="${card.url}" target="_blank">${card.url.substring(0, 30)}...</a></td>
                        <td>${cat ? cat.name : '-'}</td>
                        <td>${card.sortOrder}</td>
                        <td>
                            <button class="btn btn-sm btn-info" onclick="editCard(${card.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="deleteCard(${card.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
            }).join('');
        }
    });
}

function showCardModal() {
    fetch(`${API_BASE}/admin/categories`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                const select = document.getElementById('cardCategory');
                select.innerHTML = result.data.map(c => 
                    `<option value="${c.id}">${c.name}</option>`
                ).join('');
                document.getElementById('cardForm').reset();
                document.getElementById('cardId').value = '';
                currentCardId = null;
                $('#cardModal').modal('show');
            }
        });
}

function editCard(id) {
    Promise.all([
        fetch(`${API_BASE}/admin/cards/${id}`, { headers: getAuthHeaders() }),
        fetch(`${API_BASE}/admin/categories`, { headers: getAuthHeaders() })
    ])
    .then(([cardRes, catsRes]) => Promise.all([cardRes.json(), catsRes.json()]))
    .then(([cardResult, catsResult]) => {
        if (cardResult.success && catsResult.success) {
            const select = document.getElementById('cardCategory');
            select.innerHTML = catsResult.data.map(c => 
                `<option value="${c.id}">${c.name}</option>`
            ).join('');

            document.getElementById('cardId').value = cardResult.data.id;
            document.getElementById('cardName').value = cardResult.data.name;
            document.getElementById('cardUrl').value = cardResult.data.url;
            document.getElementById('cardDescription').value = cardResult.data.description || '';
            document.getElementById('cardLogo').value = cardResult.data.logo || '';
            document.getElementById('cardCategory').value = cardResult.data.categoryId;
            document.getElementById('cardSort').value = cardResult.data.sortOrder;
            currentCardId = id;
            $('#cardModal').modal('show');
        }
    });
}

function parseWebsite() {
    const url = document.getElementById('cardUrl').value;
    if (!url) {
        showMessage('请先输入网址', 'warning');
        return;
    }

    fetch(`${API_BASE}/admin/cards/parse`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ url })
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            if (result.data.name) document.getElementById('cardName').value = result.data.name;
            if (result.data.description) document.getElementById('cardDescription').value = result.data.description;
            if (result.data.logo) document.getElementById('cardLogo').value = result.data.logo;
            showMessage('解析成功！');
        } else {
            showMessage(result.message || '解析失败，请稍后再试。', 'danger');
        }
    })
    .catch(() => showMessage('解析网站信息失败，请稍后再试。', 'danger'));
}

function saveCard() {
    const id = document.getElementById('cardId').value;
    const data = {
        name: document.getElementById('cardName').value,
        url: document.getElementById('cardUrl').value,
        description: document.getElementById('cardDescription').value,
        logo: document.getElementById('cardLogo').value,
        categoryId: parseInt(document.getElementById('cardCategory').value),
        sortOrder: parseInt(document.getElementById('cardSort').value)
    };

    const url = id ? `${API_BASE}/admin/cards/${id}` : `${API_BASE}/admin/cards`;
    const method = id ? 'PUT' : 'POST';

    fetch(url, {
        method,
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            $('#cardModal').modal('hide');
            showMessage(result.message || '卡片保存成功');
            loadCards();
        } else {
            showMessage(result.message || '卡片保存失败', 'danger');
        }
    })
    .catch(() => showMessage('保存卡片时出现异常，请稍后再试。', 'danger'));
}

function deleteCard(id) {
    if (!confirm('确定要删除这张卡片吗？')) return;

    fetch(`${API_BASE}/admin/cards/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage(result.message || '卡片删除成功');
            loadCards();
        } else {
            showMessage(result.message || '卡片删除失败', 'danger');
        }
    })
    .catch(() => showMessage('删除卡片时出现异常，请稍后再试。', 'danger'));
}

function loadAds() {
    fetch(`${API_BASE}/admin/ads`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                const tbody = document.getElementById('adsTable');
                tbody.innerHTML = result.data.map(ad => `
                    <tr>
                        <td>${ad.id}</td>
                        <td>${ad.title}</td>
                        <td>${ad.position}</td>
                        <td>${ad.enabled ? '<span class="badge badge-success">启用</span>' : '<span class="badge badge-secondary">禁用</span>'}</td>
                        <td>${ad.sortOrder}</td>
                        <td>
                            <button class="btn btn-sm btn-info" onclick="editAd(${ad.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="deleteAd(${ad.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

function showAdModal() {
    document.getElementById('adForm').reset();
    document.getElementById('adId').value = '';
    currentAdId = null;
    $('#adModal').modal('show');
}

function editAd(id) {
    fetch(`${API_BASE}/admin/ads/${id}`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('adId').value = result.data.id;
                document.getElementById('adTitle').value = result.data.title;
                document.getElementById('adImage').value = result.data.imageUrl || '';
                document.getElementById('adLink').value = result.data.linkUrl || '';
                document.getElementById('adPosition').value = result.data.position;
                document.getElementById('adEnabled').value = result.data.enabled.toString();
                document.getElementById('adSort').value = result.data.sortOrder;
                currentAdId = id;
                $('#adModal').modal('show');
            }
        });
}

function saveAd() {
    const id = document.getElementById('adId').value;
    const data = {
        title: document.getElementById('adTitle').value,
        imageUrl: document.getElementById('adImage').value,
        linkUrl: document.getElementById('adLink').value,
        position: document.getElementById('adPosition').value,
        enabled: document.getElementById('adEnabled').value === 'true',
        sortOrder: parseInt(document.getElementById('adSort').value)
    };

    const url = id ? `${API_BASE}/admin/ads/${id}` : `${API_BASE}/admin/ads`;
    const method = id ? 'PUT' : 'POST';

    fetch(url, {
        method,
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            $('#adModal').modal('hide');
            showMessage(result.message || '广告保存成功');
            loadAds();
        } else {
            showMessage(result.message || '广告保存失败', 'danger');
        }
    })
    .catch(() => showMessage('保存广告信息时出现异常，请稍后再试。', 'danger'));
}

function deleteAd(id) {
    if (!confirm('确定要删除这个广告吗？')) return;

    fetch(`${API_BASE}/admin/ads/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage(result.message || '广告删除成功');
            loadAds();
        } else {
            showMessage(result.message || '广告删除失败', 'danger');
        }
    })
    .catch(() => showMessage('删除广告时出现异常，请稍后再试。', 'danger'));
}

function loadFriendLinks() {
    fetch(`${API_BASE}/admin/friendlinks`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                const tbody = document.getElementById('friendlinksTable');
                tbody.innerHTML = result.data.map(link => `
                    <tr>
                        <td>${link.id}</td>
                        <td>${link.name}</td>
                        <td><a href="${link.url}" target="_blank">${link.url.substring(0, 40)}...</a></td>
                        <td>${link.sortOrder}</td>
                        <td>
                            <button class="btn btn-sm btn-info" onclick="editFriendLink(${link.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="deleteFriendLink(${link.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        });
}

function showFriendLinkModal() {
    document.getElementById('friendLinkForm').reset();
    document.getElementById('friendLinkId').value = '';
    currentFriendLinkId = null;
    $('#friendLinkModal').modal('show');
}

function editFriendLink(id) {
    fetch(`${API_BASE}/admin/friendlinks/${id}`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('friendLinkId').value = result.data.id;
                document.getElementById('friendLinkName').value = result.data.name;
                document.getElementById('friendLinkUrl').value = result.data.url;
                document.getElementById('friendLinkLogo').value = result.data.logo || '';
                document.getElementById('friendLinkDescription').value = result.data.description || '';
                document.getElementById('friendLinkSort').value = result.data.sortOrder;
                currentFriendLinkId = id;
                $('#friendLinkModal').modal('show');
            }
        });
}

function saveFriendLink() {
    const id = document.getElementById('friendLinkId').value;
    const data = {
        name: document.getElementById('friendLinkName').value,
        url: document.getElementById('friendLinkUrl').value,
        logo: document.getElementById('friendLinkLogo').value,
        description: document.getElementById('friendLinkDescription').value,
        sortOrder: parseInt(document.getElementById('friendLinkSort').value)
    };

    const url = id ? `${API_BASE}/admin/friendlinks/${id}` : `${API_BASE}/admin/friendlinks`;
    const method = id ? 'PUT' : 'POST';

    fetch(url, {
        method,
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            $('#friendLinkModal').modal('hide');
            showMessage(result.message || '友链保存成功');
            loadFriendLinks();
        } else {
            showMessage(result.message || '友链保存失败', 'danger');
        }
    })
    .catch(() => showMessage('保存友链信息时出现异常，请稍后再试。', 'danger'));
}

function deleteFriendLink(id) {
    if (!confirm('确定要删除这个友链吗？')) return;

    fetch(`${API_BASE}/admin/friendlinks/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage(result.message || '友链删除成功');
            loadFriendLinks();
        } else {
            showMessage(result.message || '友链删除失败', 'danger');
        }
    })
    .catch(() => showMessage('删除友链时出现异常，请稍后再试。', 'danger'));
}

function loadBackups() {
    fetch(`${API_BASE}/admin/backup/list`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            const tbody = document.getElementById('backupsTable');
            if (!tbody) {
                return;
            }

            if (result.success) {
                if (result.data.length) {
                    tbody.innerHTML = result.data.map(backup => `
                        <tr>
                            <td>${backup.name}</td>
                            <td>${(backup.size / 1024 / 1024).toFixed(2)} MB</td>
                            <td>${new Date(backup.date).toLocaleString()}</td>
                            <td>
                                <a href="${API_BASE}/admin/backup/download/${backup.name}" class="btn btn-sm btn-success" download>
                                    <i class="fas fa-download"></i> 下载
                                </a>
                                <button class="btn btn-sm btn-danger" onclick="deleteBackup('${backup.name}')">
                                    <i class="fas fa-trash"></i> 删除
                                </button>
                            </td>
                        </tr>
                    `).join('');
                } else {
                    tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">暂无备份文件</td></tr>';
                }
            } else {
                showMessage(result.message || '获取备份列表失败', 'danger');
            }
        })
        .catch(() => showMessage('获取备份列表失败，请稍后再试。', 'danger'));
}

function createBackup() {
    if (!confirm('确定要创建备份吗？')) return;

    fetch(`${API_BASE}/admin/backup/create`, {
        method: 'POST',
        headers: getAuthHeaders()
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage('备份创建成功：' + result.data);
            loadBackups();
        } else {
            showMessage(result.message || '备份创建失败', 'danger');
        }
    })
    .catch(() => showMessage('创建备份时出现异常，请稍后再试。', 'danger'));
}

function restoreBackup() {
    const fileInput = document.getElementById('restoreFile');
    if (!fileInput.files.length) {
        showMessage('请选择备份文件', 'warning');
        return;
    }

    if (!confirm('确定要恢复备份吗？这将覆盖当前数据！')) return;

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch(`${API_BASE}/admin/backup/restore`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage('备份恢复成功！');
            setTimeout(() => location.reload(), 1200);
        } else {
            showMessage(result.message || '备份恢复失败', 'danger');
        }
    })
    .catch(() => showMessage('恢复备份时出现异常，请稍后再试。', 'danger'));
}

function deleteBackup(filename) {
    if (!confirm('确定要删除这个备份文件吗？')) return;

    fetch(`${API_BASE}/admin/backup/delete/${filename}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage('备份删除成功！');
            loadBackups();
        } else {
            showMessage(result.message || '备份删除失败', 'danger');
        }
    })
    .catch(() => showMessage('删除备份文件时出现异常，请稍后再试。', 'danger'));
}

function showAutoBackupModal() {
    fetch(`${API_BASE}/admin/backup/auto-config`, { headers: getAuthHeaders() })
        .then(r => r.json())
        .then(result => {
            if (result.success) {
                document.getElementById('autoBackupEnabled').checked = result.data.enabled;
                document.getElementById('autoBackupDays').value = result.data.days;
                document.getElementById('autoBackupMonths').value = result.data.months;
                
                const lastBackupInfo = document.getElementById('lastBackupInfo');
                if (result.data.lastBackup) {
                    lastBackupInfo.innerHTML = '<small>上次自动备份时间：' + new Date(result.data.lastBackup).toLocaleString() + '</small>';
                } else {
                    lastBackupInfo.innerHTML = '<small>尚未执行过自动备份</small>';
                }
                
                $('#autoBackupModal').modal('show');
            }
        });
}

function saveAutoBackupConfig() {
    const config = {
        enabled: document.getElementById('autoBackupEnabled').checked,
        days: parseInt(document.getElementById('autoBackupDays').value),
        months: parseInt(document.getElementById('autoBackupMonths').value)
    };

    if (config.days === 0 && config.months === 0 && config.enabled) {
        showMessage('请至少设置天数或月数！', 'warning');
        return;
    }

    fetch(`${API_BASE}/admin/backup/auto-config`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(config)
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage('自动备份配置保存成功！');
            $('#autoBackupModal').modal('hide');
        } else {
            showMessage(result.message || '自动备份配置保存失败', 'danger');
        }
    })
    .catch(() => showMessage('保存自动备份配置时出现异常，请稍后再试。', 'danger'));
}


document.getElementById('passwordForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        showMessage('两次输入的密码不一致！', 'warning');
        return;
    }

    fetch(`${API_BASE}/admin/users/password`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify({ newPassword })
    })
    .then(r => r.json())
    .then(result => {
        if (result.success) {
            showMessage(result.message || '密码修改成功！');
            this.reset();
        } else {
            showMessage(result.message || '密码修改失败', 'danger');
        }
    })
    .catch(() => showMessage('修改密码时出现异常，请稍后再试。', 'danger'));
});

loadDashboard();
