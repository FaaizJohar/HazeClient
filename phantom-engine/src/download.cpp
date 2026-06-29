#include "phantom/download.hpp"
#include <iostream>

namespace phantom {

PhantomDownloadEngine::PhantomDownloadEngine(std::shared_ptr<AdaptiveResourceGovernor> governor)
    : m_governor(std::move(governor)), m_max_concurrent_downloads(4), m_chunk_size(1024 * 1024) {}

Result<void> PhantomDownloadEngine::initialize(const HardwareProfile& profile) noexcept {
    configure_for_tier();
    return {};
}

void PhantomDownloadEngine::suspend() noexcept {
    // Pause all non-critical downloads
    m_max_concurrent_downloads = 0;
}

void PhantomDownloadEngine::resume() noexcept {
    configure_for_tier();
}

void PhantomDownloadEngine::queue_download(const DownloadRequest& req, std::function<void(Result<void>)> on_complete) {
    // In production, uses QUIC/HTTP3 multiplexing via a background task graph
    // Validates SHA-256 while streaming to avoid second pass
    std::cout << "[DownloadEngine] Queueing " << req.url << " (Chunk size: " << m_chunk_size << ")\n";
    on_complete({}); // Mock completion
}

void PhantomDownloadEngine::configure_for_tier() {
    auto tier = m_governor->get_current_tier();
    if (tier == MemoryTier::Feather) {
        m_max_concurrent_downloads = 2;
        m_chunk_size = 256 * 1024; // 256KB chunks for low-end
    } else if (tier == MemoryTier::Ghost) {
        m_max_concurrent_downloads = 0;
    } else {
        m_max_concurrent_downloads = 8;
        m_chunk_size = 4 * 1024 * 1024; // 4MB chunks for high-end
    }
}

} // namespace phantom
