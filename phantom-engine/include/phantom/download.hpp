#pragma once

#include "phantom/module.hpp"
#include "phantom/governor.hpp"
#include <string>
#include <vector>
#include <functional>

namespace phantom {

struct DownloadRequest {
    std::string url;
    std::string expected_sha256;
    size_t size_bytes;
};

class PhantomDownloadEngine : public IModule {
public:
    explicit PhantomDownloadEngine(std::shared_ptr<AdaptiveResourceGovernor> governor);
    ~PhantomDownloadEngine() override = default;

    Result<void> initialize(const HardwareProfile& profile) noexcept override;
    void suspend() noexcept override;
    void resume() noexcept override;

    // Queue a download with adaptive chunk sizing
    void queue_download(const DownloadRequest& req, std::function<void(Result<void>)> on_complete);

private:
    std::shared_ptr<AdaptiveResourceGovernor> m_governor;
    size_t m_max_concurrent_downloads;
    size_t m_chunk_size;

    void configure_for_tier();
};

} // namespace phantom
