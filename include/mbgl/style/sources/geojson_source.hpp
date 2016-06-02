#pragma once

#include <mbgl/style/source.hpp>

namespace mbgl {

class Tileset;

namespace style {

class GeoJSONSource : public Source {
public:
    GeoJSONSource(const std::string& id);

    // Future public API:
    // void setData(FeatureCollection&&);
    // void setJSON(const std::string& json);
    // void loadData(const std::string& url);


    // Private implementation

    class Impl;

    template <class Fn>
    GeoJSONSource(Fn&& fn)
        : Source(SourceType::GeoJSON, fn(*this)) {
    }
};

} // namespace style
} // namespace mbgl
